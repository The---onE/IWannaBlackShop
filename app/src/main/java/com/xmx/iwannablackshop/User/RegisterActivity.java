package com.xmx.iwannablackshop.User;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.avos.avoscloud.AVACL;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.SaveCallback;
import com.xmx.iwannablackshop.ActivityBase.BaseTempActivity;
import com.xmx.iwannablackshop.R;

public class RegisterActivity extends BaseTempActivity {

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_register);
    }

    @Override
    protected void setListener() {
        getViewById(R.id.register_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText nn = getViewById(R.id.register_nickname);
                final String nickname = nn.getText().toString();
                EditText un = getViewById(R.id.register_username);
                final String username = un.getText().toString();
                EditText pw = getViewById(R.id.register_password);
                final String password = pw.getText().toString();
                EditText pw2 = getViewById(R.id.register_password2);
                String password2 = pw2.getText().toString();

                if (nickname.equals("")) {
                    showToast("请输入昵称");
                    return;
                }
                if (username.equals("")) {
                    showToast("请输入用户名");
                    return;
                }
                if (password.equals("")) {
                    showToast("请输入密码");
                    return;
                }
                if (!password.equals(password2)) {
                    showToast("两次输入密码不一致");
                    return;
                }

                AVQuery<AVObject> query = AVQuery.getQuery("UserInf");
                query.whereEqualTo("username", username);
                query.countInBackground(new CountCallback() {
                    public void done(int count, AVException e) {
                        if (e == null) {
                            if (count > 0) {
                                showToast("该用户名已被注册");
                            } else {
                                try {
                                    final AVObject post = new AVObject("UserInf");

                                    post.put("username", username);
                                    post.put("password", UserManager.getSHA(password));
                                    post.put("nickname", nickname);
                                    post.put("status", 0);
                                    post.put("timestamp", System.currentTimeMillis() / 1000);

                                    final String checksum = UserManager.makeChecksum();
                                    post.put("checksum", UserManager.getSHA(checksum));

                                    AVACL acl = new AVACL();
                                    acl.setPublicReadAccess(true);
                                    acl.setPublicWriteAccess(true);
                                    post.setACL(acl);

                                    post.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(AVException e) {
                                            if (e == null) {
                                                showToast("注册成功");

                                                UserManager.getInstance().setId(post.getObjectId());
                                                UserManager.getInstance().saveChecksum(checksum);
                                                UserManager.getInstance().login();

                                                finish();
                                            } else {
                                                filterException(e);
                                            }
                                        }
                                    });
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                            }
                        } else {
                            filterException(e);
                        }
                    }
                });
            }
        });

        getViewById(R.id.register_cancel).setOnClickListener(new View.OnClickListener() {
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
