package com.xmx.iwannablackshop.Item;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.PushService;
import com.avos.avoscloud.SaveCallback;
import com.xmx.iwannablackshop.ActivityBase.BaseNavigationActivity;
import com.xmx.iwannablackshop.Chat.ChatroomActivity;
import com.xmx.iwannablackshop.PushMessage.PushItemMessageActivity;
import com.xmx.iwannablackshop.PushMessage.ReceiveMessageActivity;
import com.xmx.iwannablackshop.R;
import com.xmx.iwannablackshop.User.Callback.AutoLoginCallback;
import com.xmx.iwannablackshop.User.UserManager;

import java.util.ArrayList;
import java.util.List;

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
                UserManager.getInstance().checkLogin(new AutoLoginCallback() {
                    @Override
                    public void success(AVObject user) {
                        List<String> subscribing = user.getList("subscribing");
                        if (subscribing == null) {
                            subscribing = new ArrayList<>();
                        }

                        if (subscribing.contains(title)) {
                            showToast("已经关注过了");
                            PushService.subscribe(getBaseContext(), UserManager.getSHA(title), ReceiveMessageActivity.class);
                            AVInstallation.getCurrentInstallation().saveInBackground();
                        } else {
                            subscribing.add(title);
                            user.put("subscribing", subscribing);
                            user.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(AVException e) {
                                    if (e == null) {
                                        PushService.subscribe(getBaseContext(), UserManager.getSHA(title), ReceiveMessageActivity.class);
                                        AVInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(AVException e) {
                                                if (e == null) {
                                                    showToast("关注成功");
                                                } else {
                                                    filterException(e);
                                                }
                                            }
                                        });
                                    } else {
                                        filterException(e);
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void notLoggedIn() {
                        showToast("请登录");
                    }

                    @Override
                    public void errorNetwork() {
                        showToast("网络连接失败");
                    }

                    @Override
                    public void errorUsername() {
                        showToast("请登录");
                    }

                    @Override
                    public void errorChecksum() {
                        showToast("请重新登录");
                    }
                });
            }
        });

        Button unsubscribe = getViewById(R.id.unsubscribe);
        unsubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PushService.unsubscribe(getBaseContext(), UserManager.getSHA(title));
                AVInstallation.getCurrentInstallation().saveInBackground();
                UserManager.getInstance().checkLogin(new AutoLoginCallback() {
                    @Override
                    public void success(AVObject user) {
                        List<String> subscribing = user.getList("subscribing");
                        if (subscribing == null) {
                            showToast("没有关注过");
                        } else {
                            if (!subscribing.contains(title)) {
                                showToast("没有关注过");
                            } else {
                                subscribing.remove(title);
                                user.put("subscribing", subscribing);
                                user.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(AVException e) {
                                        if (e == null) {
                                            showToast("取关成功");
                                        } else {
                                            filterException(e);
                                        }
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void notLoggedIn() {
                        showToast("请登录");
                    }

                    @Override
                    public void errorNetwork() {
                        showToast("网络连接失败");
                    }

                    @Override
                    public void errorUsername() {
                        showToast("请登录");
                    }

                    @Override
                    public void errorChecksum() {
                        showToast("请重新登录");
                    }
                });
            }
        });
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }
}
