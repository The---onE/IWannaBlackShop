package com.xmx.iwannablackshop;

/**
 * Created by The_onE on 2016/1/1.
 */
public class Item {
    String id = null;
    String title = "";
    String tag = "";

    Item(String i, String ti) {
        id = i;
        title = ti;
    }

    Item(String i, String ti, String ta) {
        id = i;
        title = ti;
        tag = ta;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getTag() {
        return tag;
    }

    @Override
    public boolean equals(Object o) {
        Item other = (Item) o;
        return id.equals(other.id);
    }
}
