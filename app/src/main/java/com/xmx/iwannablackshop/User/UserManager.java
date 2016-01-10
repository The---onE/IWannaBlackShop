package com.xmx.iwannablackshop.User;

import android.content.Context;
import android.content.SharedPreferences;

import java.security.MessageDigest;
import java.util.Random;

/**
 * Created by The_onE on 2016/1/10.
 */
public class UserManager {
    public static String getSHA(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(s.getBytes("UTF-8"));
            byte[] digest = md.digest();
            return String.format("%064x", new java.math.BigInteger(1, digest));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String makeChecksum() {
        int checksum = new Random().nextInt();
        return "" + checksum;
    }

    public static void saveChecksum(Context context, String checksum) {
        SharedPreferences sp = context.getSharedPreferences("USER", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("checksum", checksum);
        editor.apply();
    }

    public static String getChecksum(Context context) {
        SharedPreferences sp = context.getSharedPreferences("USER", Context.MODE_PRIVATE);
        return sp.getString("checksum", "");
    }

    public static void saveId(Context context, String id) {
        SharedPreferences sp = context.getSharedPreferences("USER", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("id", id);
        editor.apply();
    }

    public static String getId(Context context) {
        SharedPreferences sp = context.getSharedPreferences("USER", Context.MODE_PRIVATE);
        return sp.getString("id", "");
    }

    public static void login(Context context) {
        SharedPreferences sp = context.getSharedPreferences("USER", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("loggedin", true);
        editor.apply();
    }

    public static void logout(Context context) {
        SharedPreferences sp = context.getSharedPreferences("USER", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("loggedin", false);
        editor.putString("checksum", "");
        editor.apply();
    }

    public static boolean isLoggedIn(Context context) {
        SharedPreferences sp = context.getSharedPreferences("USER", Context.MODE_PRIVATE);
        return sp.getBoolean("loggedin", false);
    }

    public static void setNickname(Context context, String nickname) {
        SharedPreferences sp = context.getSharedPreferences("USER", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("nickname", nickname);
        editor.apply();
    }

    public static String getNickname(Context context) {
        SharedPreferences sp = context.getSharedPreferences("USER", Context.MODE_PRIVATE);
        String nickname = sp.getString("nickname", "");
        return nickname.equals("") ? "匿名" : nickname;
    }
}
