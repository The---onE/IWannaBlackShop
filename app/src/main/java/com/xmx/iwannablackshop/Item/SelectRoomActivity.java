package com.xmx.iwannablackshop.Item;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.xmx.iwannablackshop.ActivityBase.BaseNavigationActivity;
import com.xmx.iwannablackshop.Chat.AVImClientManager;
import com.xmx.iwannablackshop.Chat.ChatroomActivity;
import com.xmx.iwannablackshop.R;

import java.util.Arrays;

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

        /*AVQuery<AVObject> query = new AVQuery<>("Item");
        query.getInBackground(id, new GetCallback<AVObject>() {
            public void done(AVObject post, AVException e) {
                if (e == null) {
                    getSupportActionBar().setTitle(post.getString("title"));
                } else {
                    filterException(e);
                }
            }
        });*/

        setTitle(title);
    }

    @Override
    protected void setListener() {
        Button chatroom = getViewById(R.id.chatroom_button);
        chatroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selfId = getSharedPreferences("MEMBER", Context.MODE_PRIVATE).getString("self", "XMX");
                AVImClientManager.getInstance().open(selfId, new AVIMClientCallback() {
                    @Override
                    public void done(AVIMClient avimClient, AVIMException e) {
                        if (e == null) {
                            startActivity(ChatroomActivity.class,
                                    "id", id,
                                    "title", title);
                        } else {
                            filterException(e);
                            showToast("进入失败");
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }
}
