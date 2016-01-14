package com.xmx.iwannablackshop.Chat;

import android.os.Bundle;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.xmx.iwannablackshop.ActivityBase.BaseTempActivity;
import com.xmx.iwannablackshop.Chat.Callback.CreateConversationCallback;
import com.xmx.iwannablackshop.R;
import com.xmx.iwannablackshop.User.UserManager;

public class SideTextActivity extends BaseTempActivity {
    private AVIMConversation squareConversation;
    private ChatFragment chatFragment;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_side_text);

        String title = getIntent().getStringExtra("user");
        setTitle(title);
        chatFragment = (ChatFragment) getFragmentManager().findFragmentById(R.id.fragment_chat);
    }

    @Override
    protected void setListener() {
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        if (AVImClientManager.getInstance().getClient() == null) {
            String nickname = UserManager.getInstance().getNickname();
            AVImClientManager.getInstance().open(nickname, new AVIMClientCallback() {
                @Override
                public void done(AVIMClient avimClient, AVIMException e) {
                    if (e == null) {
                        String user = getIntent().getStringExtra("user");
                        initSideText(user);
                    } else {
                        filterException(e);
                        showToast("进入失败");
                    }
                }
            });
        } else {
            String user = getIntent().getStringExtra("user");
            initSideText(user);
        }
    }

    private void initSideText(final String user) {
        AVImClientManager.getInstance().createSideText(user,
                new CreateConversationCallback() {
                    @Override
                    public void success(AVIMConversation conversation) {
                        squareConversation = conversation;
                        chatFragment.setConversation(squareConversation);
                    }

                    @Override
                    public void failure(Exception e) {
                        filterException(e);
                    }
                });
    }
}
