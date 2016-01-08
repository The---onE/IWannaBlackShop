package com.xmx.iwannablackshop.Chat;

import android.content.Context;
import android.content.Intent;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMMessageHandler;
import com.xmx.iwannablackshop.Chat.Event.ImMessageEvent;

import de.greenrobot.event.EventBus;

/**
 * Created by The_onE on 2016/1/4.
 */
public class MessageBaseHandler extends AVIMMessageHandler {
    private Context context;

    public MessageBaseHandler(Context context) {
        this.context = context;
    }

    @Override
    public void onMessage(AVIMMessage message, AVIMConversation conversation, AVIMClient client) {
        String clientID;
        try {
            clientID = AVImClientManager.getInstance().getClientId();
            if (client.getClientId().equals(clientID)) {

                // 过滤掉自己发的消息
                if (!message.getFrom().equals(clientID)) {
                    sendEvent(message, conversation);
                    if (NotificationUtils.isShowNotification(conversation.getConversationId())) {
                        sendNotification(message, conversation);
                    }
                }
            } else {
                client.close(null);
            }
        } catch (IllegalStateException e) {
            client.close(null);
        }
    }

    /**
     * 因为没有 db，所以暂时先把消息广播出去，由接收方自己处理
     * 稍后应该加入 db
     *
     * @param message
     * @param conversation
     */
    private void sendEvent(AVIMMessage message, AVIMConversation conversation) {
        ImMessageEvent event = new ImMessageEvent();
        event.message = message;
        event.conversation = conversation;
        EventBus.getDefault().post(event);
    }

    private void sendNotification(final AVIMMessage message, final AVIMConversation conversation) {
        final String id = conversation.getName();

        AVQuery<AVObject> query = new AVQuery<>("Item");
        query.getInBackground(id, new GetCallback<AVObject>() {
            public void done(AVObject post, AVException e) {
                if (e == null) {
                    String from = message.getFrom();
                    String content = message.getContent();

                    Intent intent = new Intent(context, NotificationBroadcastReceiver.class);
                    intent.putExtra("id", id);
                    intent.putExtra("title", post.getString("title"));
                    NotificationUtils.showNotification(context, post.getString("title"),
                            from + " : " + content, null, intent);
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}
