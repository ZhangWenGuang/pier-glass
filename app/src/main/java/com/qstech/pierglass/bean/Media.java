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

    public Media(int time, Uri imageUri) {
        this.time = time;
        this.imageUri = imageUri;
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
}
