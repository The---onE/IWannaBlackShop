package com.xmx.iwannablackshop.Chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.xmx.iwannablackshop.MainActivity;

/**
 * Created by wli on 15/9/8.
 * 因为 notification 点击时，控制权不在 app，此时如果 app 被 kill 或者上下文改变后，
 * 有可能对 notification 的响应会做相应的变化，所以此处将所有 notification 都发送至此类，
 * 然后由此类做分发。
 */
public class NotificationBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (AVImClientManager.getInstance().getClient() == null) {
            gotoMainActivity(context);
        } else {
            String type = intent.getStringExtra("type");
            if (type != null) {
                if (type.equals("side-text")) {
                    String user = intent.getStringExtra("user");
                    if (!TextUtils.isEmpty(user)) {
                        gotoSideTextActivity(context, intent);
                    }
                } else if (type.equals("chatroom")) {
                    String id = intent.getStringExtra("id");
                    if (!TextUtils.isEmpty(id)) {
                        gotoChatroomActivity(context, intent);
                    }
                }
            }
        }
    }

    /**
     * 如果 app 上下文已经缺失，则跳转到登陆页面，走重新登陆的流程
     *
     * @param context
     */
    private void gotoMainActivity(Context context) {
        Intent startActivityIntent = new Intent(context, MainActivity.class);
        startActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(startActivityIntent);
    }

    private void gotoSideTextActivity(final Context context, final Intent intent) {
        Intent start = new Intent(context, SideTextActivity.class);
        start.putExtra("user", intent.getStringExtra("user"));
        start.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(start);
    }

    /**
     * 跳转至广场页面
     *
     * @param context
     * @param intent
     */
    private void gotoChatroomActivity(final Context context, final Intent intent) {
        Intent start = new Intent(context, ChatroomActivity.class);
        start.putExtra("id", intent.getStringExtra("id"));
        start.putExtra("title", intent.getStringExtra("title"));
        start.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(start);
    }
}
