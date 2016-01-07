package com.xmx.iwannablackshop.Chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;
import com.xmx.iwannablackshop.Item.SelectRoomActivity;
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
      gotoLoginActivity(context);
    } else {
      String conversationId = intent.getStringExtra(Constants.CONVERSATION_ID);
      if (!TextUtils.isEmpty(conversationId)) {
//        if ("5688d14860b2a099cdd60397".equals(conversationId)) {
          gotoSquareActivity(context, intent, conversationId);
//        } else {
//          gotoSingleChatActivity(context, intent);
//        }
      }
    }
  }

  /**
   * 如果 app 上下文已经缺失，则跳转到登陆页面，走重新登陆的流程
   * @param context
   */
  private void gotoLoginActivity(Context context) {
    Intent startActivityIntent = new Intent(context, MainActivity.class);
    startActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    context.startActivity(startActivityIntent);
  }

  /**
   * 跳转至广场页面
   * @param context
   * @param intent
   */
  private void gotoSquareActivity(final Context context, Intent intent, final String id) {
      AVQuery<AVObject> query = new AVQuery<>("Item");
      query.getInBackground(id, new GetCallback<AVObject>() {
          public void done(AVObject post, AVException e) {
              if (e == null) {
                  Intent start = new Intent(context, SelectRoomActivity.class);
                  start.putExtra("id", id);
                  start.putExtra("title", post.getString("title"));
                  start.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                  context.startActivity(start);
              } else {
                  e.printStackTrace();
              }
          }
      });
  }

  /**
   * 跳转至单聊页面
   * @param context
   * @param intent
   */
//  private void gotoSingleChatActivity(Context context, Intent intent) {
//    Intent startActivityIntent = new Intent(context, SelectRoomActivity.class);
//    startActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//    startActivityIntent.putExtra(Constants.MEMBER_ID, intent.getStringExtra(Constants.MEMBER_ID));
//    context.startActivity(startActivityIntent);
//  }
}
