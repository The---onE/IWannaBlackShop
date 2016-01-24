package com.xmx.iwannablackshop.Item;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.PushService;
import com.xmx.iwannablackshop.ActivityBase.BaseNavigationActivity;
import com.xmx.iwannablackshop.Chat.ChatroomActivity;
import com.xmx.iwannablackshop.PushMessage.PushItemMessageActivity;
import com.xmx.iwannablackshop.PushMessage.ReceiveMessageActivity;
import com.xmx.iwannablackshop.R;
import com.xmx.iwannablackshop.User.UserManager;

public class SelectRoomActivity extends BaseNavigationActivity {
    String id;
    String title;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_select_room);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        id = getIntent().getStringExtra("id");
        title = getIntent().getStringExtra("title");

        setTitle(title);
    }

    @Override
    protected void setListener() {
        Button chatroom = getViewById(R.id.chatroom_button);
        chatroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(ChatroomActivity.class,
                        "id", id,
                        "title", title);
            }
        });

        Button push = getViewById(R.id.push_message);
        push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(PushItemMessageActivity.class,
                        "title", title);
            }
        });

        Button subscribe = getViewById(R.id.subscribe);
        subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PushService.subscribe(getBaseContext(), UserManager.getSHA(title), ReceiveMessageActivity.class);
                AVInstallation.getCurrentInstallation().saveInBackground();
            }
        });

        Button unsubscribe = getViewById(R.id.unsubscribe);
        unsubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PushService.unsubscribe(getBaseContext(), UserManager.getSHA(title));
                AVInstallation.getCurrentInstallation().saveInBackground();
            }
        });
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }
}
