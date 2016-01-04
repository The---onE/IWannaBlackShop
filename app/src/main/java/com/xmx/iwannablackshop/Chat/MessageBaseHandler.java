package com.xmx.iwannablackshop.Chat;

import android.content.Context;
import android.content.Intent;

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
        String clientID = "";
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

    private void sendNotification(AVIMMessage message, AVIMConversation conversation) {
        String notificationContent = message.getContent();

        Intent intent = new Intent(context, NotificationBroadcastReceiver.class);
        intent.putExtra(Constants.CONVERSATION_ID, conversation.getConversationId());
        intent.putExtra(Constants.MEMBER_ID, message.getFrom());
        NotificationUtils.showNotification(context, "", notificationContent, null, intent);
    }
}
