package com.qstech.pierglass.bean;

import android.net.Uri;

import com.qstech.pierglass.utils.TimeUtils;

/**
 * Created by wgzhang on 2016/9/22.
 * media元素属性
 */

public class Media {
    private int time;
    private Uri imageUri;
    private int viewType;

    public Media(int time, Uri imageUri, int viewType) {
        this.time = time;
        this.imageUri = imageUri;
        this.viewType = viewType;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getTimeString() {
        return TimeUtils.toString(time);
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    /**
     *
     * @param time 时间长度
     * @param imageUri 图片路径
     * @param viewType 菜单类型
     */

    public void setAll(int time, Uri imageUri, int viewType) {
        this.time = time;
        this.imageUri = imageUri;
        this.viewType = viewType;
    }
}
