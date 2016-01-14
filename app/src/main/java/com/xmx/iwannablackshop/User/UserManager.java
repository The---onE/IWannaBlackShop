package com.xmx.iwannablackshop.User;

import android.content.Context;
import android.content.SharedPreferences;

import com.avos.avoscloud.AVACL;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.xmx.iwannablackshop.Chat.AVImClientManager;
import com.xmx.iwannablackshop.User.Callback.AutoLoginCallback;
import com.xmx.iwannablackshop.User.Callback.LoginCallback;
import com.xmx.iwannablackshop.User.Callback.RegisterCallback;

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

    public String getUsername() {
        return mSP.getString("username", "");
    }

    public void login(String un, String cs, String nn) {
        SharedPreferences.Editor editor = mSP.edit();
        editor.putBoolean("loggedin", true);
        editor.putString("username", un);
        editor.putString("checksum", cs);
        editor.putString("nickname", nn);
        editor.apply();

        saveLog(un);

        openClient(nn);
    }

    public void logout() {
        SharedPreferences.Editor editor = mSP.edit();
        editor.putBoolean("loggedin", false);
        editor.putString("username", "");
        editor.putString("checksum", "");
        editor.putString("nickname", "");
        editor.apply();

        closeClient();
    }

    public void saveLog(String username) {
        final AVObject post = new AVObject("LoginLog");
        post.put("username", username);
        post.put("status", 0);
        post.put("timestamp", System.currentTimeMillis() / 1000);

        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e != null) {
                    e.printStackTrace();
                }
            }
        });
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

    public void register(final String username, final String password, final String nickname, final RegisterCallback registerCallback) {
        final AVQuery<AVObject> query = AVQuery.getQuery("UserInf");
        query.whereEqualTo("username", username);
        query.countInBackground(new CountCallback() {
            public void done(final int count, AVException e) {
                if (e == null) {
                    if (count > 0) {
                        registerCallback.usernameExist();
                    } else {
                        AVQuery<AVObject> query2 = AVQuery.getQuery("UserData");
                        query2.whereEqualTo("nickname", nickname);
                        query2.countInBackground(new CountCallback() {
                            @Override
                            public void done(int i, AVException e) {
                                if (e == null) {
                                    if (i > 0) {
                                        registerCallback.nicknameExist();
                                    } else {
                                        final AVObject post = new AVObject("UserInf");
                                        post.put("username", username);
                                        post.put("password", UserManager.getSHA(password));
                                        post.put("status", 0);
                                        post.put("timestamp", System.currentTimeMillis() / 1000);

                                        final AVObject data = new AVObject("UserData");
                                        data.put("username", username);
                                        data.put("nickname", nickname);
                                        final String checksum = UserManager.makeChecksum();
                                        data.put("checksumA", UserManager.getSHA(checksum));

                                        post.put("data", data);

                                        AVACL acl = new AVACL();
                                        acl.setPublicReadAccess(true);
                                        acl.setPublicWriteAccess(false);
                                        post.setACL(acl);

                                        post.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(AVException e) {
                                                if (e == null) {
                                                    login(username, checksum, nickname);
                                                    registerCallback.success();
                                                } else {
                                                    registerCallback.errorNetwork();
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    registerCallback.errorNetwork();
                                }
                            }
                        });
                    }
                } else {
                    registerCallback.errorNetwork();
                }
            }
        });
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
                            user.getAVObject("data").fetchIfNeededInBackground(new GetCallback<AVObject>() {
                                @Override
                                public void done(AVObject data, AVException e) {
                                    if (e == null) {
                                        final String nickname = data.getString("nickname");
                                        final String newChecksum = makeChecksum();
                                        data.put("checksumA", getSHA(newChecksum));
                                        data.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(AVException e) {
                                                if (e == null) {
                                                    login(username, newChecksum, nickname);
                                                    loginCallback.success(user);
                                                } else {
                                                    loginCallback.errorNetwork();
                                                }
                                            }
                                        });
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
        final String username = getUsername();
        if (!isLoggedIn() || username.equals("")) {
            loginCallback.notLoggedIn();
            return;
        }
        AVQuery<AVObject> query = new AVQuery<>("UserData");
        query.whereEqualTo("username", username);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        final AVObject user = list.get(0);
                        String checksum = user.getString("checksumA");
                        if (checksum.equals(getSHA(getChecksum()))) {
                            final String nickname = user.getString("nickname");
                            final String newChecksum = makeChecksum();
                            user.put("checksumA", getSHA(newChecksum));
                            user.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(AVException e) {
                                    if (e == null) {
                                        login(username, newChecksum, nickname);
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
                        loginCallback.errorUsername();
                    }
                } else {
                    loginCallback.errorNetwork();
                }
            }
        });
    }

    public void checkLogin(final AutoLoginCallback loginCallback) {
        String username = getUsername();
        if (!isLoggedIn() || username.equals("")) {
            loginCallback.notLoggedIn();
            return;
        }
        AVQuery<AVObject> query = new AVQuery<>("UserData");
        query.whereEqualTo("username", username);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        AVObject user = list.get(0);
                        String checksum = user.getString("checksumA");
                        if (checksum.equals(getSHA(getChecksum()))) {
                            loginCallback.success(user);
                        } else {
                            logout();
                            loginCallback.errorChecksum();
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
}
