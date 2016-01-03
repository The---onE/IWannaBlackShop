package com.xmx.iwannablackshop.Chat;

/**
 * Created by wli on 15/8/23.
 * 用来存放各种 static final 值
 */
public class Constants {

  private static final String LEANMESSAGE_CONSTANTS_PREFIX = "com.xmx.iwannablackshop.Chat";

  public static final String MEMBER_ID = getPrefixConstant("member_id");
  public static final String CONVERSATION_ID = getPrefixConstant("conversation_id");

  private static String getPrefixConstant(String str) {
    return LEANMESSAGE_CONSTANTS_PREFIX + str;
  }
}
