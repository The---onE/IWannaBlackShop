package com.xmx.iwannablackshop.User;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.avos.avoscloud.AVObject;
import com.xmx.iwannablackshop.ActivityBase.BaseTempActivity;
import com.xmx.iwannablackshop.R;

public class LoginActivity extends BaseTempActivity {

    @Override
    protected void onResume() {
        super.onResume();

        UserManager.getInstance().autoLogin(
                new AutoLoginCallback() {
                    @Override
                    public void success(AVObject user) {
                        showToast("登录成功");
                        finish();
                    }

                    @Override
                    public void notLoggedIn() {

                    }

                    @Override
                    public void errorNetwork() {
                        showToast("网络连接失败");
                    }

                    @Override
                    public void errorChecksum() {
                        showToast("请重新登录");
                    }
                });
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void setListener() {
        getViewById(R.id.login_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText un = getViewById(R.id.login_username);
                final String username = un.getText().toString();
                EditText pw = getViewById(R.id.login_password);
                final String password = pw.getText().toString();

                if (username.equals("")) {
                    showToast("请输入用户名");
                    return;
                }
                if (password.equals("")) {
                    showToast("请输入密码");
                    return;
                }

                UserManager.getInstance().login(username, password,
                        new LoginCallback() {
                            @Override
                            public void success(AVObject user) {
                                showToast("登录成功");
                                finish();
                            }

                            @Override
                            public void errorNetwork() {
                                showToast("网络连接失败");
                            }

                            @Override
                            public void errorUsername() {
                                showToast("用户不存在");
                            }

                            @Override
                            public void errorPassword() {
                                showToast("密码错误");
                            }
                        });
            }
        });

        getViewById(R.id.login_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(RegisterActivity.class);
            }
        });
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }
}
