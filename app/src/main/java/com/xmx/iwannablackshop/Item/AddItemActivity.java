package com.xmx.iwannablackshop.Item;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.SaveCallback;
import com.xmx.iwannablackshop.ActivityBase.BaseTempActivity;
import com.xmx.iwannablackshop.R;

/**
 * Created by The_onE on 2015/12/28.
 */
public class AddItemActivity extends BaseTempActivity {
    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_additem);
    }

    @Override
    protected void setListener() {
        getViewById(R.id.add_item_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText title = getViewById(R.id.add_item_title);
                if (title.getText().toString().equals("")) {
                    showToast("必须添加标题");
                    return;
                }

                AVQuery<AVObject> query = AVQuery.getQuery("Item");
                query.whereEqualTo("title", title.getText());
                query.countInBackground(new CountCallback() {
                    public void done(int count, AVException e) {
                        if (e == null) {
                            if (count > 0) {
                                showToast("已经添加过该分类");
                            } else {
                                AVObject post = new AVObject("Item");


                                String selfId = getSharedPreferences("MEMBER", Context.MODE_PRIVATE).getString("self", "XMX");

                                EditText tag = getViewById(R.id.add_item_tag);
                                post.put("title", title.getText());
                                post.put("tag", tag.getText());
                                post.put("status", 0);
                                post.put("pubUser", selfId);
                                post.put("pubTimestamp", System.currentTimeMillis() / 1000);
                                post.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(AVException e) {
                                        if (e == null) {
                                            showToast("添加分类成功");
                                            finish();
                                        } else {
                                            filterException(e);
                                        }
                                    }
                                });
                            }
                        } else {
                            filterException(e);
                        }
                    }
                });

            }
        });

        getViewById(R.id.add_item_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }
}
