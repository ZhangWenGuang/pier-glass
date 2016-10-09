package com.qstech.pierglass.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonFloat;
import com.qstech.pierglass.App;
import com.qstech.pierglass.R;
import com.qstech.pierglass.adapter.MenuAdapter;
import com.qstech.pierglass.bean.Media;
import com.qstech.pierglass.listener.OnItemClickListener;
import com.qstech.pierglass.view.ListViewDecoration;
import com.yanzhenjie.recyclerview.swipe.Closeable;
import com.yanzhenjie.recyclerview.swipe.OnSwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemMoveListener;

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

    private MenuAdapter mMenuAdapter;

    private ButtonFloat newButton;

    private ButtonFloat playButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mContext = this;
        mMediaDatas = new ArrayList<>();

        SwipeMenuRecyclerView swipeMenuRecyclerView = (SwipeMenuRecyclerView) findViewById(R.id.recycler_view);
        //mLinearLayoutManager = new LinearLayoutManager(this);
        //swipeMenuRecyclerView.setLayoutManager(mLinearLayoutManager); //布局管理器
        swipeMenuRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        swipeMenuRecyclerView.setHasFixedSize(true); //如果item够简单，高度是确定的，打开fixsize将提高性能。
        swipeMenuRecyclerView.setItemAnimator(new DefaultItemAnimator()); //设置item默认动画，加也行，不加也行。
        swipeMenuRecyclerView.addItemDecoration(new ListViewDecoration()); //添加分割线

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

        newButton = (ButtonFloat) findViewById(R.id.button_new);

        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MediaActivity.class);
                startActivityForResult(intent, App.MEDIA_ADD);
            }
        });
    }

    /**
     * 当Item移动的时候。
     */
    private OnItemMoveListener onItemMoveListener = new OnItemMoveListener() {
        @Override
        public boolean onItemMove(int fromPosition, int toPosition) {
            //当Item被拖拽的时候。
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
            {
                SwipeMenuItem deleteItem = new SwipeMenuItem(mContext)
                        .setBackgroundDrawable(R.drawable.selector_red)
                        .setImage(R.mipmap.ic_action_delete)
                        .setText("delete")
                        .setTextColor(Color.WHITE)
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
            Toast.makeText(mContext, "第" + position + "条！", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(mContext, MediaActivity.class);
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
                Toast.makeText(mContext, "list第" + adapterPosition + "：右侧菜单第" + menuPosition, Toast.LENGTH_SHORT).show();
            } else if (direction == SwipeMenuRecyclerView.LEFT_DIRECTION) {
                Toast.makeText(mContext, "list第" + adapterPosition + "：左侧菜单第" + menuPosition, Toast.LENGTH_SHORT).show();
            }

            // TODO 如果是删除：推荐调用Adapter.notifyItemRemoved(position)，不推荐Adapter.notifyDataSetChanged();
            if (menuPosition == 0) {// 删除按钮被点击。
                mMediaDatas.remove(adapterPosition);
                mMenuAdapter.notifyItemRemoved(adapterPosition);
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) ;
        {
            finish();
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("-------------", requestCode + "");
        if (requestCode == App.MEDIA_ALTER && resultCode == RESULT_OK) {
            Uri imageUri = Uri.parse(data.getStringExtra("imageUri"));
            int imageTime = data.getIntExtra("imageTime", 0);
            int position = data.getIntExtra("position", 0);
            mMediaDatas.get(position).setImageUri(imageUri);
            mMediaDatas.get(position).setTime(imageTime);
            Log.d("-------------", mMediaDatas.get(position).getTimeString());
            mMenuAdapter.notifyDataSetChanged();
        }
        if (requestCode == App.MEDIA_ADD && resultCode == RESULT_OK) {
            Uri imageUri = Uri.parse(data.getStringExtra("imageUri"));
            int imageTime = data.getIntExtra("imageTime", 0);
            Media media = new Media(imageTime, imageUri);
            Log.d("-------------", media.getTimeString());
            mMediaDatas.add(media);
            mMenuAdapter.notifyDataSetChanged();
        }
    }
}
