package com.xmx.iwannablackshop.Item;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.PushService;
import com.xmx.iwannablackshop.ActivityBase.BaseNavigationActivity;
import com.xmx.iwannablackshop.Chat.ChatroomActivity;
import com.xmx.iwannablackshop.PushMessage.ReceiveMessageActivity;
import com.xmx.iwannablackshop.R;

public class SelectRoomActivity extends BaseNavigationActivity {
    String id;
    String title;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        title = getIntent().getStringExtra("title");
        PushService.unsubscribe(this, title);
        AVInstallation.getCurrentInstallation().saveInBackground();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_select_room);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        id = getIntent().getStringExtra("id");
        title = getIntent().getStringExtra("title");

        setTitle(title);
        PushService.subscribe(this, title, ReceiveMessageActivity.class);
        AVInstallation.getCurrentInstallation().saveInBackground();
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
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }
}
