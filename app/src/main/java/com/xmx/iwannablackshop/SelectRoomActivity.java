package com.xmx.iwannablackshop;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;

public class SelectRoomActivity extends BaseNavigationActivity {

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_select_room);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String id = getIntent().getStringExtra("id");

        AVQuery<AVObject> query = new AVQuery<AVObject>("Item");
        query.getInBackground(id, new GetCallback<AVObject>() {
            public void done(AVObject post, AVException e) {
                if (e == null) {
                    getSupportActionBar().setTitle(post.getString("title"));
                } else {
                    filterException(e);
                }
            }
        });
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }
}
