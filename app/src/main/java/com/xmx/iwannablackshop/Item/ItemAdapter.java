package com.xmx.iwannablackshop.Item;

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

    ArrayList<Item> mItems = new ArrayList<>();
    Context mContext;

    public ItemAdapter(Context context, ArrayList<Item> items) {
        mContext = context;
        mItems = items;
    }

    public void setItems(ArrayList<Item> items) {
        mItems = items;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return Math.max(mItems.size(), 1);
    }

    @Override
    public Object getItem(int position) {
        if (position < mItems.size()) {
            return mItems.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        TextView title;
        TextView tag;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_item, null);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.item_title);
            holder.tag = (TextView) convertView.findViewById(R.id.item_tag);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (position < mItems.size()) {
            holder.title.setText(mItems.get(position).getTitle());
            holder.title.setTextColor(Color.BLACK);
            holder.tag.setText(mItems.get(position).getTag());
        } else {
            holder.title.setText("加载失败");
            holder.tag.setText("");
        }
        return convertView;
    }
}
