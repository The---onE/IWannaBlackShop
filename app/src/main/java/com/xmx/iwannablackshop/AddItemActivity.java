package com.xmx.iwannablackshop;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.SaveCallback;

/**
 * Created by The_onE on 2015/12/28.
 */
public class AddItemActivity extends BaseActivity {
    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.add_item_main);

        getViewById(R.id.add_item_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AVObject post = new AVObject("Item");
                EditText title = getViewById(R.id.add_item_title);
                if (!title.getText().toString().equals("")) {
                    post.put("content", title.getText());
                } else {
                    showToast("必须添加标题");
                    return;
                }
                EditText tag = getViewById(R.id.add_item_tag);
                post.put("tag", tag.getText());
                post.put("status", 0);
                post.put("pubUser", "XMX");
                post.put("pubTimestamp", android.os.SystemClock.uptimeMillis());
                post.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null) {
                            showToast("添加分类成功");
                        } else {
                            filterException(e);
                        }
                        finish();
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
    protected void setListener() {

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }
}
