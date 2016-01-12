package com.xmx.iwannablackshop.Chat;

import android.content.Context;
import android.content.Intent;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.AVIMTypedMessageHandler;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.xmx.iwannablackshop.Chat.Event.ImTypeMessageEvent;
import com.xmx.iwannablackshop.R;

import de.greenrobot.event.EventBus;

/**
 * Created by zhangxiaobo on 15/4/20.
 */
public class MessageHandler extends AVIMTypedMessageHandler<AVIMTypedMessage> {

    private Context context;

    public MessageHandler(Context context) {
        this.context = context;
    }

    @Override
    public void onMessage(AVIMTypedMessage message, AVIMConversation conversation, AVIMClient client) {
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
    private void sendEvent(AVIMTypedMessage message, AVIMConversation conversation) {
        ImTypeMessageEvent event = new ImTypeMessageEvent();
        event.message = message;
        event.conversation = conversation;
        EventBus.getDefault().post(event);
    }

    private void sendNotification(final AVIMTypedMessage message, AVIMConversation conversation) {
        final String notificationContent = message instanceof AVIMTextMessage ?
                ((AVIMTextMessage) message).getText() : context.getString(R.string.unsupported_message_type);

        final String id = conversation.getName();

        AVQuery<AVObject> query = new AVQuery<>("Item");
        query.getInBackground(id, new GetCallback<AVObject>() {
            public void done(AVObject post, AVException e) {
                if (e == null) {
                    String from = message.getFrom();
                    String content = notificationContent;

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
