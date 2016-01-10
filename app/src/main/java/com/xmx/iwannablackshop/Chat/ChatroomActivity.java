package com.xmx.iwannablackshop.Chat;

import android.os.Bundle;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;
import com.xmx.iwannablackshop.ActivityBase.BaseTempActivity;
import com.xmx.iwannablackshop.Chat.Event.LeftChatItemClickEvent;
import com.xmx.iwannablackshop.R;
import com.xmx.iwannablackshop.User.UserManager;

import java.util.ArrayList;
import java.util.List;

public class ChatroomActivity extends BaseTempActivity {
    private AVIMConversation squareConversation;
    private ChatFragment chatFragment;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_chatroom);

        String title = getIntent().getStringExtra("title");
        setTitle(title);
        chatFragment = (ChatFragment) getFragmentManager().findFragmentById(R.id.fragment_chat);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        if (AVImClientManager.getInstance().getClient() == null) {
            String nickname = UserManager.getInstance().getNickname();
            AVImClientManager.getInstance().open(nickname, new AVIMClientCallback() {
                @Override
                public void done(AVIMClient avimClient, AVIMException e) {
                    if (e == null) {
                        String id = getIntent().getStringExtra("id");
                        initSquare(id);
                    } else {
                        filterException(e);
                        showToast("进入失败");
                    }
                }
            });
        } else {
            String id = getIntent().getStringExtra("id");
            initSquare(id);
        }
    }

    /**
     * 根据 conversationId 查取本地缓存中的 conversation，如若没有缓存，则返回一个新建的 conversaiton
     */
    private void initSquare(final String id) {
        final AVIMClient client = AVImClientManager.getInstance().getClient();
        final String selfId = AVImClientManager.getInstance().getClientId();

        AVIMConversationQuery query = client.getQuery();
        query.whereEqualTo("name", id);
        query.findInBackground(new AVIMConversationQueryCallback() {
            @Override
            public void done(List<AVIMConversation> convs, AVIMException e) {
                if (e == null) {
                    if (convs != null && !convs.isEmpty()) {
                        squareConversation = convs.get(0);
                        if (squareConversation.getMembers().contains(selfId)) {
                            chatFragment.setConversation(squareConversation);
                        } else {
                            joinSquare();
                        }
                    } else {
                        client.createConversation(new ArrayList<String>(), id, null,
                                new AVIMConversationCreatedCallback() {
                                    @Override
                                    public void done(AVIMConversation avimConversation, AVIMException e) {
                                        if (e == null) {
                                            squareConversation = avimConversation;
                                            joinSquare();
                                        } else {
                                            filterException(e);
                                        }
                                    }
                                });
                    }
                } else {
                    filterException(e);
                }
            }
        });
    }

    /**
     * 加入 conversation
     */
    private void joinSquare() {
        squareConversation.join(new AVIMConversationCallback() {
            @Override
            public void done(AVIMException e) {
                if (filterException(e)) {
                    chatFragment.setConversation(squareConversation);
                }
            }
        });
    }

    /**
     * 处理聊天 item 点击事件，点击后跳转到相应1对1的对话
     */
    public void onEvent(LeftChatItemClickEvent event) {
//        Intent intent = new Intent(this, AVSingleChatActivity.class);
//        intent.putExtra(Constants.MEMBER_ID, event.userId);
//        startActivity(intent);
    }
}
