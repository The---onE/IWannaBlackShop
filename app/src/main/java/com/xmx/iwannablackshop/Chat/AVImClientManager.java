package com.xmx.iwannablackshop.Chat;

import android.text.TextUtils;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;
import com.xmx.iwannablackshop.Chat.Callback.CreateConversationCallback;
import com.xmx.iwannablackshop.Chat.Callback.FindConversationCallback;
import com.xmx.iwannablackshop.Chat.Callback.JoinConversationCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wli on 15/8/13.
 */
public class AVImClientManager {

    private static AVImClientManager imClientManager;

    private AVIMClient avimClient;
    private String clientId;
    private boolean openFlag = false;

    public synchronized static AVImClientManager getInstance() {
        if (null == imClientManager) {
            imClientManager = new AVImClientManager();
        }
        return imClientManager;
    }

    private AVImClientManager() {
    }

    public void open(String clientId, AVIMClientCallback callback) {
        this.clientId = clientId;
        avimClient = AVIMClient.getInstance(clientId);
        avimClient.open(callback);
        openFlag = true;
    }

    public void close() {
        if (openFlag) {
            avimClient = AVIMClient.getInstance(clientId);
            avimClient.close(new AVIMClientCallback() {
                @Override
                public void done(AVIMClient avimClient, AVIMException e) {

                }
            });
            openFlag = false;
            avimClient = null;
            clientId = "匿名";
        }
    }

    public void joinConversation(final AVIMConversation conversation, final JoinConversationCallback callback) {
        if (!conversation.getMembers().contains(getClientId())) {
            conversation.join(new AVIMConversationCallback() {
                @Override
                public void done(AVIMException e) {
                    if (e == null) {
                        callback.success(conversation);
                    } else {
                        callback.failure(e);
                    }
                }
            });
        } else {
            callback.success(conversation);
        }
    }

    public void quitConversation(AVIMConversation conversation) {
        conversation.quit(new AVIMConversationCallback() {
            @Override
            public void done(AVIMException e) {
                if (e != null) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void findConversation(String name, final FindConversationCallback callback) {
        AVIMConversationQuery query = getClient().getQuery();
        query.whereEqualTo("name", name);
        query.findInBackground(new AVIMConversationQueryCallback() {
            @Override
            public void done(List<AVIMConversation> convs, AVIMException e) {
                if (e == null) {
                    if (convs != null && !convs.isEmpty()) {
                        callback.found(convs.get(0));
                    } else {
                        callback.notFound();
                    }
                } else {
                    callback.error(e);
                }
            }
        });
    }

    public void createConversation(String name, final CreateConversationCallback callback) {
        avimClient.createConversation(new ArrayList<String>(), name, null,
                new AVIMConversationCreatedCallback() {
                    @Override
                    public void done(AVIMConversation avimConversation, AVIMException e) {
                        if (e == null) {
                            callback.success(avimConversation);
                        } else {
                            callback.failure(e);
                        }
                    }
                });
    }

    public AVIMClient getClient() {
        return avimClient;
    }

    public String getClientId() {
        if (TextUtils.isEmpty(clientId)) {
            throw new IllegalStateException("Please call AVImClientManager.open first");
        }
        return clientId;
    }
}
