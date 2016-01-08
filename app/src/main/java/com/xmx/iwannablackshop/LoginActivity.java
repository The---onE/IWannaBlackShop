package com.xmx.iwannablackshop;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.xmx.iwannablackshop.ActivityBase.BaseTempActivity;
import com.xmx.iwannablackshop.Chat.AVImClientManager;

public class LoginActivity extends BaseTempActivity {

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_login);

        String selfId = getSharedPreferences("MEMBER", Context.MODE_PRIVATE).getString("self", "XMX");
        EditText title = getViewById(R.id.login_username);
        title.setText(selfId);
    }

    @Override
    protected void setListener() {
        getViewById(R.id.login_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText title = getViewById(R.id.login_username);
                final String username = title.getText().toString();
                if (username.equals("")) {
                    showToast("请输入昵称");
                    return;
                }

                SharedPreferences sp = getSharedPreferences("MEMBER", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("self", username);
                editor.apply();

                AVImClientManager.getInstance().open(username, new AVIMClientCallback() {
                    @Override
                    public void done(AVIMClient avimClient, AVIMException e) {
                        if (e != null) {
                            filterException(e);
                        }
                    }
                });

                finish();
            }
        });

        getViewById(R.id.login_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }
}
