package com.xmx.iwannablackshop;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.xmx.iwannablackshop.Chat.MessageBaseHandler;
import com.xmx.iwannablackshop.Chat.MessageHandler;

/**
 * Created by The_onE on 2016/1/3.
 */
public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AVOSCloud.initialize(this, "jg8rpu25f2dTGU4dSWLo96tg-gzGzoHsz", "6NdDmnjpXWSID9LCFzBO3CPj");
        AVIMMessageManager.registerMessageHandler(AVIMTextMessage.class, new MessageHandler(this));
        AVIMMessageManager.registerMessageHandler(AVIMMessage.class, new MessageBaseHandler(this));
    }
}
