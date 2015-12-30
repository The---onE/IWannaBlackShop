package com.xmx.iwannablackshop.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xmx.iwannablackshop.R;

import java.util.ArrayList;

/**
 * Created by The_onE on 2015/12/29.
 */
public class ItemAdapter extends BaseAdapter {

    ArrayList<String> mTitles = new ArrayList<>();
    Context mContext;

    public ItemAdapter(Context context, ArrayList<String> titles) {
        mContext = context;
        mTitles = titles;
    }

    public void setTitles(ArrayList<String> titles) {
        mTitles = titles;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return Math.max(mTitles.size(), 1);
    }

    @Override
    public Object getItem(int position) {
        return mTitles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        TextView tv;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final int p = position;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_item, null);
            holder = new ViewHolder();
            holder.tv = (TextView) convertView.findViewById(R.id.item_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (position < mTitles.size()) {
            holder.tv.setText(mTitles.get(position));
            holder.tv.setTextColor(Color.BLACK);
        } else {
            holder.tv.setText("加载失败");
        }
        return convertView;
    }
}
