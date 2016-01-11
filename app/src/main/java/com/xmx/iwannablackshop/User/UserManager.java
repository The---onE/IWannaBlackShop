package com.xmx.iwannablackshop.User;

import android.content.Context;
import android.content.SharedPreferences;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.xmx.iwannablackshop.Chat.AVImClientManager;

import java.security.MessageDigest;
import java.util.List;
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

    public void login(String id, String cs, String nn) {
        SharedPreferences.Editor editor = mSP.edit();
        editor.putBoolean("loggedin", true);
        editor.putString("id", id);
        editor.putString("checksum", cs);
        editor.putString("nickname", nn);
        editor.apply();

        openClient(nn);
    }

    public void logout() {
        SharedPreferences.Editor editor = mSP.edit();
        editor.putBoolean("loggedin", false);
        editor.putString("id", "");
        editor.putString("checksum", "");
        editor.putString("nickname", "");
        editor.apply();

        closeClient();
    }

    public boolean isLoggedIn() {
        return mSP.getBoolean("loggedin", false);
    }

    public String getNickname() {
        String nickname = mSP.getString("nickname", "");
        return nickname.equals("") ? "匿名" : nickname;
    }

    public void openClient(String nickname) {
        AVImClientManager.getInstance().open(nickname, new AVIMClientCallback() {
            @Override
            public void done(AVIMClient avimClient, AVIMException e) {
                if (e != null) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void closeClient() {
        AVImClientManager.getInstance().close();
    }

    public void login(final String username, final String password, final LoginCallback loginCallback) {
        AVQuery<AVObject> query = new AVQuery<>("UserInf");
        query.whereEqualTo("username", username);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> avObjects, AVException e) {
                if (e == null) {
                    if (avObjects.size() > 0) {
                        final AVObject user = avObjects.get(0);
                        String rightPassword = user.getString("password");
                        if (rightPassword.equals(getSHA(password))) {
                            final String newChecksum = makeChecksum();
                            final String nickname = user.getString("nickname");
                            user.put("checksum", getSHA(newChecksum));
                            user.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(AVException e) {
                                    if (e == null) {
                                        login(user.getObjectId(), newChecksum, nickname);
                                        loginCallback.success(user);
                                    } else {
                                        loginCallback.errorNetwork();
                                    }
                                }
                            });
                        } else {
                            loginCallback.errorPassword();
                        }
                    } else {
                        loginCallback.errorUsername();
                    }
                } else {
                    loginCallback.errorNetwork();
                }
            }
        });
    }

    public void autoLogin(final AutoLoginCallback loginCallback) {
        final String id = getId();
        if (!isLoggedIn() || id.equals("")) {
            loginCallback.notLoggedIn();
            return;
        }
        AVQuery<AVObject> query = new AVQuery<>("UserInf");
        query.getInBackground(id, new GetCallback<AVObject>() {
            @Override
            public void done(final AVObject user, AVException e) {
                if (e == null) {
                    String checksum = user.getString("checksum");
                    if (checksum.equals(getSHA(getChecksum()))) {
                        final String newChecksum = makeChecksum();
                        final String nickname = user.getString("nickname");
                        user.put("checksum", getSHA(newChecksum));
                        user.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e == null) {
                                    login(id, newChecksum, nickname);
                                    loginCallback.success(user);
                                } else {
                                    loginCallback.errorNetwork();
                                }
                            }
                        });
                    } else {
                        logout();
                        loginCallback.errorChecksum();
                    }
                } else {
                    loginCallback.errorNetwork();
                }
            }
        });
    }

    public void checkLogin(final AutoLoginCallback loginCallback) {
        String id = getId();
        if (!isLoggedIn() || id.equals("")) {
            loginCallback.notLoggedIn();
            return;
        }
        AVQuery<AVObject> query = new AVQuery<>("UserInf");
        query.getInBackground(id, new GetCallback<AVObject>() {
            @Override
            public void done(final AVObject user, AVException e) {
                if (e == null) {
                    String checksum = user.getString("checksum");
                    if (checksum.equals(getSHA(getChecksum()))) {
                        loginCallback.success(user);
                    } else {
                        logout();
                        loginCallback.errorChecksum();
                    }
                } else {
                    loginCallback.errorNetwork();
                }
            }
        });
    }
}
