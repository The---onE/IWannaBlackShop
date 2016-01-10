package com.xmx.iwannablackshop.User;

import android.content.Context;
import android.content.SharedPreferences;

import java.security.MessageDigest;
import java.util.Random;

/**
 * Created by The_onE on 2016/1/10.
 */
public class UserManager {
    private static UserManager instance;

    Context mContext;
    SharedPreferences mSP;

    public synchronized static UserManager getInstance() {
        if (null == instance) {
            instance = new UserManager();
        }
        return instance;
    }

    public void setContext(Context context) {
        mContext = context;
        mSP = context.getSharedPreferences("USER", Context.MODE_PRIVATE);
    }

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

    public void saveChecksum(String checksum) {
        SharedPreferences.Editor editor = mSP.edit();
        editor.putString("checksum", checksum);
        editor.apply();
    }

    public String getChecksum() {
        return mSP.getString("checksum", "");
    }

    public void setId(String id) {
        SharedPreferences.Editor editor = mSP.edit();
        editor.putString("id", id);
        editor.apply();
    }

    public String getId() {
        return mSP.getString("id", "");
    }

    public void login() {
        SharedPreferences.Editor editor = mSP.edit();
        editor.putBoolean("loggedin", true);
        editor.apply();
    }

    public void logout() {
        SharedPreferences.Editor editor = mSP.edit();
        editor.putBoolean("loggedin", false);
        editor.putString("checksum", "");
        editor.putString("nickname", "");
        editor.apply();
    }

    public boolean isLoggedIn() {
        return mSP.getBoolean("loggedin", false);
    }

    public void setNickname(String nickname) {
        SharedPreferences.Editor editor = mSP.edit();
        editor.putString("nickname", nickname);
        editor.apply();
    }

    public String getNickname() {
        String nickname = mSP.getString("nickname", "");
        return nickname.equals("") ? "匿名" : nickname;
    }
}
