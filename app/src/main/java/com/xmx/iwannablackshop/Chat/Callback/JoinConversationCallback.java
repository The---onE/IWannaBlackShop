package com.xmx.iwannablackshop.Chat.Callback;

import com.avos.avoscloud.im.v2.AVIMConversation;

/**
 * Created by The_onE on 2016/1/13.
 */
public abstract class JoinConversationCallback {
    public JoinConversationCallback() {
    }

    public abstract void success(AVIMConversation conversation);

    public abstract void failure(Exception e);
}
