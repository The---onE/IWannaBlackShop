package com.xmx.iwannablackshop;

import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.xmx.iwannablackshop.Adapter.ItemAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGARefreshViewHolder;

public class MainActivity extends BaseNavigationActivity
        implements BGARefreshLayout.BGARefreshLayoutDelegate {

    final static int LOAD_LIMIT = 20;

    BGARefreshLayout mRefreshLayout;
    ListView mItemList;
    ItemAdapter mItemAdapter;
    boolean loadedFlag = false;
    boolean allFlag = false;
    ArrayList<Item> mItems = new ArrayList<>();

    private long exitTime = 0;
    static long LONGEST_EXIT_TIME = 2000;

    @Override
    protected void onResume() {
        super.onResume();
        onBGARefreshLayoutBeginRefreshing(mRefreshLayout);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);

        Button addItem = getViewById(R.id.add_item);
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(AddItemActivity.class);
            }
        });

        initItemList();

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    @Override
    protected void setListener() {
        mItemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Item item = (Item) parent.getItemAtPosition(position);
                if (item != null && item.getId() != null) {
                    startActivity(SelectRoomActivity.class,
                            "id", item.getId(),
                            "title", item.getTitle());
                }
            }
        });
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if ((System.currentTimeMillis() - exitTime) > LONGEST_EXIT_TIME) {
                showToast(R.string.confirm_exit);
                exitTime = System.currentTimeMillis();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            showToast("Press Camera");
        } else if (id == R.id.nav_gallery) {
            showToast("Press Gallery");
        } else if (id == R.id.nav_slideshow) {
            showToast("Press Nav_slideshow");
        } else if (id == R.id.nav_manage) {
            showToast("Press Nav_manage");
        } else if (id == R.id.nav_share) {
            showToast("Press Nav_share");
        } else if (id == R.id.nav_send) {
            showToast("Press Nav_send");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initRefreshLayout() {
        mRefreshLayout = getViewById(R.id.item_refresh);
        // 为BGARefreshLayout设置代理
        mRefreshLayout.setDelegate(this);
        // 设置下拉刷新和上拉加载更多的风格     参数1：应用程序上下文，参数2：是否具有上拉加载更多功能
        BGARefreshViewHolder refreshViewHolder = new BGANormalRefreshViewHolder(this, true);
        // 设置下拉刷新和上拉加载更多的风格
        mRefreshLayout.setRefreshViewHolder(refreshViewHolder);

        // 设置正在加载更多时的文本
        refreshViewHolder.setLoadingMoreText("正在加载");

        // 为了增加下拉刷新头部和加载更多的通用性，提供了以下可选配置选项  -------------START
        // 设置整个加载更多控件的背景颜色资源id
        //refreshViewHolder.setLoadMoreBackgroundColorRes(loadMoreBackgroundColorRes);
        // 设置整个加载更多控件的背景drawable资源id
        //refreshViewHolder.setLoadMoreBackgroundDrawableRes(loadMoreBackgroundDrawableRes);
        // 设置下拉刷新控件的背景颜色资源id
        //refreshViewHolder.setRefreshViewBackgroundColorRes(refreshViewBackgroundColorRes);
        // 设置下拉刷新控件的背景drawable资源id
        //refreshViewHolder.setRefreshViewBackgroundDrawableRes(refreshViewBackgroundDrawableRes);
        // 设置自定义头部视图（也可以不用设置）     参数1：自定义头部视图（例如广告位）， 参数2：上拉加载更多是否可用
        //mRefreshLayout.setCustomHeaderView(mBanner, false);
        // 可选配置  -------------END
    }

    private Item createItem(AVObject avObject) {
        String id = avObject.getObjectId();
        String title = avObject.get("title").toString();
        String tag = avObject.get("tag").toString();
        return new Item(id, title, tag);
    }

    private AVQuery<AVObject> createQuery(boolean loadFlag) {
        AVQuery<AVObject> query = new AVQuery<>("Item");
        query.whereEqualTo("status", 0);
        if (loadFlag) {
            query.setSkip(mItems.size());
        }
        query.limit(LOAD_LIMIT);
        query.orderByDescending("pubTimestamp");

        return query;
    }

    private void initItemList() {
        AVOSCloud.initialize(this, "jg8rpu25f2dTGU4dSWLo96tg-gzGzoHsz", "6NdDmnjpXWSID9LCFzBO3CPj");
        AVAnalytics.trackAppOpened(getIntent());

        mItemList = getViewById(R.id.item_list);

        initRefreshLayout();

        mItems.add(new Item(null, getString(R.string.default_string)));
        mItemAdapter = new ItemAdapter(getApplicationContext(), mItems);
        mItemList.setAdapter(mItemAdapter);

        AVQuery<AVObject> query = createQuery(false);
        query.findInBackground(new FindCallback<AVObject>() {
            public void done(List<AVObject> avObjects, AVException e) {
                mItems.clear();
                if (e == null) {
                    for (int i = 0; i < avObjects.size(); ++i) {
                        Item item = createItem(avObjects.get(i));
                        mItems.add(item);
                    }
                } else {
                    filterException(e);
                }
                mItemAdapter.setItems(mItems);

                loadedFlag = true;
            }
        });
    }

    private void refresh() {
        AVQuery<AVObject> query = createQuery(false);
        query.findInBackground(new FindCallback<AVObject>() {
            public void done(List<AVObject> avObjects, AVException e) {
                if (e == null) {
                    boolean flag = false;
                    for (int i = avObjects.size() - 1; i >= 0; --i) {
                        Item item = createItem(avObjects.get(i));
                        if (!mItems.contains(item)) {
                            mItems.add(0, item);
                            flag = true;
                        }
                    }

                    if (flag) {
                        mItemAdapter.setItems(mItems);
                    } else {
                        showToast("没有更新数据");
                    }
                } else {
                    filterException(e);
                }
                mRefreshLayout.endRefreshing();
            }
        });
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout bgaRefreshLayout) {
        if (loadedFlag) {
            refresh();
        } else {
            mRefreshLayout.endRefreshing();
        }
    }

    private void loadMore() {
        AVQuery<AVObject> query = createQuery(true);
        query.findInBackground(new FindCallback<AVObject>() {
            public void done(List<AVObject> avObjects, AVException e) {
                if (e == null) {
                    boolean flag = false;
                    for (int i = 0; i < avObjects.size(); ++i) {
                        Item item = createItem(avObjects.get(i));
                        if (!mItems.contains(item)) {
                            mItems.add(item);
                            flag = true;
                        }
                    }

                    if (flag) {
                        mItemAdapter.setItems(mItems);
                    } else {
                        allFlag = true;
                        showToast("已加载全部");
                    }
                } else {
                    filterException(e);
                }
                mRefreshLayout.endLoadingMore();
            }
        });
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout bgaRefreshLayout) {
        if (loadedFlag) {
            if (!allFlag) {
                loadMore();
                return true;
            } else {
                showToast("已加载全部");
                return false;
            }
        } else {
            return false;
        }
    }
}
