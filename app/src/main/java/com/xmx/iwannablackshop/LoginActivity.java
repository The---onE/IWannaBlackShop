package com.xmx.iwannablackshop;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.SaveCallback;
import com.xmx.iwannablackshop.ActivityBase.BaseTempActivity;

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
                final EditText title = getViewById(R.id.login_username);
                if (title.getText().toString().equals("")) {
                    showToast("必须添加标题");
                    return;
                }

                SharedPreferences sp = getSharedPreferences("MEMBER", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("self", title.getText().toString());
                editor.apply();
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
