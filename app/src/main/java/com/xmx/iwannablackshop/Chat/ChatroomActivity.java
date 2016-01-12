package com.xmx.iwannablackshop.Chat;

import android.os.Bundle;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.xmx.iwannablackshop.ActivityBase.BaseTempActivity;
import com.xmx.iwannablackshop.Chat.Callback.CreateConversationCallback;
import com.xmx.iwannablackshop.Chat.Callback.FindConversationCallback;
import com.xmx.iwannablackshop.Chat.Callback.JoinConversationCallback;
import com.xmx.iwannablackshop.Chat.Event.LeftChatItemClickEvent;
import com.xmx.iwannablackshop.R;
import com.xmx.iwannablackshop.User.UserManager;

public class ChatroomActivity extends BaseTempActivity {
    private AVIMConversation squareConversation;
    private ChatFragment chatFragment;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AVImClientManager.getInstance().quitConversation(squareConversation);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_chatroom);

        String title = getIntent().getStringExtra("title");
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
                        String id = getIntent().getStringExtra("id");
                        initSquare(id);
                    } else {
                        filterException(e);
                        showToast("进入失败");
                    }
                }
            });
        } else {
            String id = getIntent().getStringExtra("id");
            initSquare(id);
        }
    }

    private void initSquare(final String id) {
        AVImClientManager.getInstance().findConversation(id,
                new FindConversationCallback() {
                    @Override
                    public void found(AVIMConversation conversation) {
                        squareConversation = conversation;
                        AVImClientManager.getInstance().joinConversation(squareConversation,
                                new JoinConversationCallback() {
                                    @Override
                                    public void success(AVIMConversation conversation) {
                                        chatFragment.setConversation(squareConversation);
                                    }

                                    @Override
                                    public void failure(Exception e) {
                                        filterException(e);
                                    }
                                });
                    }

                    @Override
                    public void notFound() {
                        AVImClientManager.getInstance().createConversation(id,
                                new CreateConversationCallback() {
                                    @Override
                                    public void success(AVIMConversation conversation) {
                                        squareConversation = conversation;
                                        AVImClientManager.getInstance().joinConversation(squareConversation,
                                                new JoinConversationCallback() {
                                                    @Override
                                                    public void success(AVIMConversation conversation) {
                                                        chatFragment.setConversation(squareConversation);
                                                    }

                                                    @Override
                                                    public void failure(Exception e) {
                                                        filterException(e);
                                                    }
                                                });
                                    }

                                    @Override
                                    public void failure(Exception e) {
                                        filterException(e);
                                    }
                                });
                    }

                    @Override
                    public void error(Exception e) {
                        filterException(e);
                    }
                });
    }

    /**
     * 处理聊天 item 点击事件，点击后跳转到相应1对1的对话
     */
    public void onEvent(LeftChatItemClickEvent event) {
//        Intent intent = new Intent(this, AVSingleChatActivity.class);
//        intent.putExtra(Constants.MEMBER_ID, event.userId);
//        startActivity(intent);
    }
}
