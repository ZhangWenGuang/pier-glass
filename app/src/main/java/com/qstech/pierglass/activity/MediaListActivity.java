package com.qstech.pierglass.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonFloat;
import com.qstech.pierglass.App;
import com.qstech.pierglass.R;
import com.qstech.pierglass.adapter.MenuAdapter;
import com.qstech.pierglass.bean.Media;
import com.qstech.pierglass.listener.OnItemClickListener;
import com.qstech.pierglass.utils.FileUtils;
import com.yanzhenjie.recyclerview.swipe.Closeable;
import com.yanzhenjie.recyclerview.swipe.OnSwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemMoveListener;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemMovementListener;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by admin on 2016/9/9.
 * 功能：添加播放列表，左滑删除操作，长按进行排序操作
 * MenuAdapter  OnItemClickListener  ListViewDecoration
 */
public class MediaListActivity extends AppCompatActivity {

    private Activity mContext;

    private List<Media> mMediaDatas;

    private int addResourceId = R.mipmap.media_add;

    private MenuAdapter mMenuAdapter;

    //private ButtonFloat newButton;

    private ButtonFloat playButton;

    public static final String ANDROID_RESOURCE = "android.resource://";
    public static final String FOREWARD_SLASH = "/";
    private static Uri resourceIdToUri(Context context, int resourceId)
    {
        return Uri.parse(ANDROID_RESOURCE + context.getPackageName() + FOREWARD_SLASH + resourceId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mContext = this;
        Media addMedia = new Media(-1, resourceIdToUri(mContext, addResourceId), MenuAdapter.VIEW_TYPE_MENU_NONE);
        //媒体数据
        mMediaDatas = new ArrayList<>();
        //添加一个空数据
        mMediaDatas.add(addMedia);

        SwipeMenuRecyclerView swipeMenuRecyclerView = (SwipeMenuRecyclerView) findViewById(R.id.recycler_view);
        //mLinearLayoutManager = new LinearLayoutManager(this);
        //swipeMenuRecyclerView.setLayoutManager(mLinearLayoutManager); //布局管理器
        swipeMenuRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        swipeMenuRecyclerView.setHasFixedSize(true); //如果item够简单，高度是确定的，打开fixsize将提高性能。
        swipeMenuRecyclerView.setItemAnimator(new DefaultItemAnimator()); //设置item默认动画，加也行，不加也行。
        //swipeMenuRecyclerView.addItemDecoration(new ListViewDecoration()); //添加分割线

        //为SwipeRecyclerView的Item创建菜单就两句话
        //设置菜单创建器
        swipeMenuRecyclerView.setSwipeMenuCreator(swipeMenuCreator);
        //设置菜单Item点击监听
        swipeMenuRecyclerView.setSwipeMenuItemClickListener(menuItemClickListener);

        mMenuAdapter = new MenuAdapter(mMediaDatas);
        mMenuAdapter.setOnItemClickListener(onItemClickListener);
        swipeMenuRecyclerView.setAdapter(mMenuAdapter);

        swipeMenuRecyclerView.setLongPressDragEnabled(true); //开启拖拽，就这么一句话。
        swipeMenuRecyclerView.setOnItemMoveListener(onItemMoveListener); //监听拖拽，更新UI。
        swipeMenuRecyclerView.setOnItemMovementListener(onItemMovementListener);//禁止按钮拖动删除

        /*newButton = (ButtonFloat) findViewById(R.id.button_new);

        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MediaActivity.class);
                startActivityForResult(intent, App.MEDIA_ADD);
            }
        });*/
    }

    /**
     * 当Item被移动之前。
     */
    public OnItemMovementListener onItemMovementListener = new OnItemMovementListener() {
        /**
         * 当Item在移动之前，获取拖拽的方向。
         * @param recyclerView     {@link RecyclerView}.
         * @param targetViewHolder target ViewHolder.
         * @return
         */
        @Override
        public int onDragFlags(RecyclerView recyclerView, RecyclerView.ViewHolder targetViewHolder) {
            // 我们让第一个不能拖拽。
            if (targetViewHolder.getAdapterPosition() == (mMediaDatas.size()-1)) {
                return OnItemMovementListener.INVALID;// 返回无效的方向。
            }
            return OnItemMovementListener.LEFT | OnItemMovementListener.RIGHT | OnItemMovementListener.UP | OnItemMovementListener.DOWN; // 可以上下左右拖拽。

        }

        @Override
        public int onSwipeFlags(RecyclerView recyclerView, RecyclerView.ViewHolder targetViewHolder) {
            // 我们让第一个不能滑动删除。
            if (targetViewHolder.getAdapterPosition() == (mMediaDatas.size()-1)) {
                return OnItemMovementListener.INVALID;// 返回无效的方向。
            }

            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {// 如果是LinearLayoutManager
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                if (linearLayoutManager.getOrientation() == LinearLayoutManager.HORIZONTAL) {// 横向的List。
                    return OnItemMovementListener.UP | OnItemMovementListener.DOWN; // 只能上下滑动删除。
                } else {// 竖向的List。
                    return OnItemMovementListener.LEFT | OnItemMovementListener.RIGHT; // 只能左右滑动删除。
                }
            }
            return OnItemMovementListener.INVALID;// 其它均返回无效的方向。
        }
    };


    /**
     * 当Item移动的时候。
     */
    private OnItemMoveListener onItemMoveListener = new OnItemMoveListener() {
        @Override
        public boolean onItemMove(int fromPosition, int toPosition) {
            //当Item被拖拽的时候。
            if (fromPosition == (mMediaDatas.size() - 1) || toPosition ==(mMediaDatas.size() - 1)) {
                return false;
            }
            Collections.swap(mMediaDatas, fromPosition, toPosition);
            mMenuAdapter.notifyItemMoved(fromPosition, toPosition);
            return true; //返回true表示处理了，返回false表示你没有处理。
        }

        @Override
        public void onItemDismiss(int position) {
            //当Item被滑动删除掉的时候，在这里是无效的，因为这里没有启用这个功能。
            //使用Menu时就不使用这个侧滑删除，两个是冲突的。
        }
    };

    /**
     * 菜单创建器
     */
    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
            int width = mContext.getResources().getDimensionPixelSize(R.dimen.item_width);

            // 设置菜单方向为竖型的。
            swipeRightMenu.setOrientation(SwipeMenu.VERTICAL);
            //MATCH_PARENT 自适应高度，保持和内容一样高；也可以指定菜单具体高度，也可以用WRAP_CONTENT.
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;

            //添加右侧的，如果不添加，则右侧不会出现菜单。
            if (viewType == MenuAdapter.VIEW_TYPE_MENU_NONE) {// 根据Adapter的ViewType来决定菜单的样式、颜色等属性、或者是否添加菜单。
                // Do noting
            } else if (viewType == MenuAdapter.VIEW_TYPE_MENU_SINGLE) {// 需要添加单个菜单的Item。
                SwipeMenuItem deleteItem = new SwipeMenuItem(mContext)
                        //.setBackgroundDrawable(R.drawable.selector_red)
                        .setImage(R.mipmap.delete)
                        //.setText("delete")
                        //.setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(0)
                        .setWeight(1);

                swipeRightMenu.addMenuItem(deleteItem); //添加一个按钮到右侧菜单。
            }
        }
    };

    private OnItemClickListener onItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(int position) {
            //Toast.makeText(mContext, "第" + position + "条！", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(mContext, MediaActivity.class);
            if (position == (mMediaDatas.size() - 1)) {
                startActivityForResult(intent, App.MEDIA_ADD);
                return;
            }
            intent.putExtra("imageUri", mMediaDatas.get(position).getImageUri().toString());
            intent.putExtra("imageTime", mMediaDatas.get(position).getTime());
            intent.putExtra("position", position);
            startActivityForResult(intent, App.MEDIA_ALTER);
        }
    };

    /**
     * 菜单点击监听。
     */
    private OnSwipeMenuItemClickListener menuItemClickListener = new OnSwipeMenuItemClickListener() {
        @Override
        public void onItemClick(Closeable closeable, int adapterPosition, int menuPosition, int direction) {
            closeable.smoothCloseMenu(); //关闭被点击的菜单。

            if (direction == SwipeMenuRecyclerView.RIGHT_DIRECTION) {
                //Toast.makeText(mContext, "list第" + adapterPosition + "：右侧菜单第" + menuPosition, Toast.LENGTH_SHORT).show();
            } else if (direction == SwipeMenuRecyclerView.LEFT_DIRECTION) {
                //Toast.makeText(mContext, "list第" + adapterPosition + "：左侧菜单第" + menuPosition, Toast.LENGTH_SHORT).show();
            }

            // TODO 如果是删除：推荐调用Adapter.notifyItemRemoved(position)，不推荐Adapter.notifyDataSetChanged();
            if (menuPosition == 0) {// 删除按钮被点击。
                mMediaDatas.remove(adapterPosition);
                mMenuAdapter.notifyItemRemoved(adapterPosition);
            }
        }
    };



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == App.MEDIA_ALTER && resultCode == RESULT_OK) {
            Uri imageUri = Uri.parse(data.getStringExtra("imageUri"));
            int imageTime = data.getIntExtra("imageTime", 0);
            int position = data.getIntExtra("position", 0);
            mMediaDatas.get(position).setImageUri(imageUri);
            mMediaDatas.get(position).setTime(imageTime);
            mMediaDatas.get(position).setViewType(MenuAdapter.VIEW_TYPE_MENU_SINGLE);
            Log.d("MediaList", "resultTime:" + mMediaDatas.get(position).getTimeString());
            mMenuAdapter.notifyDataSetChanged();
        }
        if (requestCode == App.MEDIA_ADD && resultCode == RESULT_OK) {
            Uri imageUri = Uri.parse(data.getStringExtra("imageUri"));
            int imageTime = data.getIntExtra("imageTime", 0);
            mMediaDatas.get(mMediaDatas.size() - 1)
                    .setAll(imageTime, imageUri, MenuAdapter.VIEW_TYPE_MENU_SINGLE);
            Media temp = new Media(-1, resourceIdToUri(mContext, addResourceId), MenuAdapter.VIEW_TYPE_MENU_NONE);
            mMediaDatas.add(temp);
            mMenuAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_register:
                Intent intent = new Intent(mContext, RegisterActivity.class);
                startActivityForResult(intent, 123);
                break;
            case R.id.menu_release:
                FileOutputStream out = null;
                BufferedWriter writer = null;

                try {
                    if (mMediaDatas.size() > 0){
                        out = openFileOutput("play001.lst", Context.MODE_PRIVATE);
                        writer = new BufferedWriter(new OutputStreamWriter(out));
                        writer.write(FileUtils.MediaListToFile(mMediaDatas));
                    } else {
                        Toast.makeText(this, "list is null!", Toast.LENGTH_SHORT).show();
                    }
                    
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (writer != null) {
                            writer.close();
                        }
                        if (out != null) {
                            out.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case android.R.id.home:
                finish();
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onPause() {
        Log.d("MediaList","onPause");
        super.onPause();
    }
}
