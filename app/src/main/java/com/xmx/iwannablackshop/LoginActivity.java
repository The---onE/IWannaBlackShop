package com.xmx.iwannablackshop;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.xmx.iwannablackshop.ActivityBase.BaseTempActivity;
import com.xmx.iwannablackshop.Chat.AVImClientManager;
import com.xmx.iwannablackshop.User.RegisterActivity;
import com.xmx.iwannablackshop.User.UserManager;

import java.util.List;

public class LoginActivity extends BaseTempActivity {

    @Override
    protected void onResume() {
        super.onResume();
        AVQuery<AVObject> query = new AVQuery<>("UserInf");
        String id = UserManager.getId(this);
        if (!UserManager.isLoggedIn(this) || id.equals("")) {
            return;
        }

        query.getInBackground(id, new GetCallback<AVObject>() {
            @Override
            public void done(AVObject user, AVException e) {
                if (e == null) {
                    String checksum = user.getString("checksum");
                    if (checksum.equals(UserManager.getSHA(UserManager.getChecksum(getBaseContext())))) {
                        final String newChecksum = UserManager.makeChecksum();
                        final String nickname = user.getString("nickname");
                        user.put("checksum", UserManager.getSHA(newChecksum));
                        user.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e == null) {
                                    showToast("登录成功");
                                    UserManager.saveChecksum(getBaseContext(), newChecksum);
                                    UserManager.setNickname(getBaseContext(), nickname);
                                    UserManager.login(getBaseContext());

                                    finish();
                                } else {
                                    filterException(e);
                                }
                            }
                        });
                    } else {
                        UserManager.logout(getBaseContext());
                        showToast("请重新登录");
                    }
                } else {
                    filterException(e);
                }
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

                AVQuery<AVObject> query = new AVQuery<>("UserInf");
                query.whereEqualTo("username", username);
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> avObjects, AVException e) {
                        if (e == null) {
                            if (avObjects.size() > 0) {
                                final AVObject user = avObjects.get(0);
                                String rightPassword = user.getString("password");
                                if (rightPassword.equals(UserManager.getSHA(password))) {
                                    final String newChecksum = UserManager.makeChecksum();
                                    final String nickname = user.getString("nickname");
                                    user.put("checksum", UserManager.getSHA(newChecksum));
                                    user.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(AVException e) {
                                            if (e == null) {
                                                showToast("登录成功");
                                                UserManager.setId(getBaseContext(), user.getObjectId());
                                                UserManager.saveChecksum(getBaseContext(), newChecksum);
                                                UserManager.setNickname(getBaseContext(), nickname);
                                                UserManager.login(getBaseContext());

                                                AVImClientManager.getInstance().open(nickname, new AVIMClientCallback() {
                                                    @Override
                                                    public void done(AVIMClient avimClient, AVIMException e) {
                                                        if (e != null) {
                                                            filterException(e);
                                                        }
                                                    }
                                                });

                                                finish();
                                            } else {
                                                filterException(e);
                                            }
                                        }
                                    });
                                } else {
                                    showToast("密码错误");
                                }

                            } else {
                                showToast("用户不存在");
                            }
                        } else {
                            filterException(e);
                            //showToast("请登录");
                        }
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
