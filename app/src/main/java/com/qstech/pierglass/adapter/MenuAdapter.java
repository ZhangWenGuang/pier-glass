package com.qstech.pierglass.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qstech.pierglass.R;
import com.qstech.pierglass.bean.Media;
import com.qstech.pierglass.listener.OnItemClickListener;
import com.qstech.pierglass.utils.BitmapUtils;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;

import java.util.List;

/**
 * Created by admin on 2016/9/9.
 */
public class MenuAdapter extends SwipeMenuAdapter<MenuAdapter.DefaultViewHolder> {

    public static final int VIEW_TYPE_MENU_NONE = 1;
    public static final int VIEW_TYPE_MENU_SINGLE = 2;
    public static final int VIEW_TYPE_MENU_MULTI = 3;
    public static final int VIEW_TYPE_MENU_LEFT = 4;

    private List<Media> medias;

    private OnItemClickListener mOnItemClickListener;

    public MenuAdapter(List<Media> medias) {
        this.medias = medias;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public void onBindViewHolder(MenuAdapter.DefaultViewHolder holder, int position) {
        holder.setData(medias.get(position));
        holder.setOnItemClickListener(mOnItemClickListener);
    }

    @Override
    public int getItemViewType(int position) {
        return medias.get(position).getViewType();
    }

    @Override
    public int getItemCount() {
        return medias == null ? 0 : medias.size();
    }

    @Override
    public View onCreateContentView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
    }

    @Override
    public MenuAdapter.DefaultViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {
        return new DefaultViewHolder(realContentView);
    }

    static class DefaultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvTitle;
        ImageView iv_icon;
        OnItemClickListener mOnItemClickListener;

        public DefaultViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            iv_icon = (ImageView) itemView.findViewById(R.id.iv_icon);
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.mOnItemClickListener = onItemClickListener;
        }

        public void setData(Media media) {
            if (media.getTime() == -1) {
                //使用的是系统资源文件不能压缩，这里是添加按钮图片
                this.tvTitle.setText("");
                this.iv_icon.setImageURI(media.getImageUri());
            } else {
                this.tvTitle.setText(media.getTimeString());
                //this.iv_icon.setImageURI(media.getImageUri());//内存消耗过大，需要进行图片缩小显示
                this.iv_icon.setImageBitmap(BitmapUtils.getSmallBitmap(media.getImageUri()));
            }
        }

        @Override
        public void onClick(View view) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(getAdapterPosition());
            }
        }
    }
}
