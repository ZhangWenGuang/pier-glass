package com.qstech.pierglass.utils;

import android.net.Uri;
import android.util.Log;

import com.qstech.pierglass.bean.Media;

import java.util.List;

/**
 * Created by admin on 2016/10/17.
 */

public class FileUtils {



    public static String MediaListToFile(List<Media> mediaList) {

        StringBuilder temp = new StringBuilder();

        if (mediaList.size() == 0) {
            return null;
        }
        Log.d("FileUtils", "" + (mediaList.size() + 48));
        temp.append("[all]items=" + digitalToASCII(mediaList.size()));
        for (int i=1; i<=mediaList.size(); i++ ) {
            //param = 停留时间,入屏方式,出屏方式,入屏速度,闪烁速度,闪烁次数,播放次数,播放特效,特效速度
            temp.append("[item" + digitalToASCII(i) + "]");
            temp.append("param=" + digitalToASCII(mediaList.get(i-1).getTime()) +
                    "," + digitalToASCII(1) + "," + digitalToASCII(1) + "," + digitalToASCII(1) +
                    "," + digitalToASCII(0) + "," + digitalToASCII(1) + "," + digitalToASCII(1) +
                    "," + digitalToASCII(0) + "," + digitalToASCII(1));
            //img1=x坐标,y坐标,文件名称,闪烁,显示区域宽度,显示区域高度
            //imgparam1=停留时间
            temp.append("img" + digitalToASCII(i) +"=" + digitalToASCII(0) + "," +
                    digitalToASCII(0) + "," + getMediaName(mediaList.get(i-1).getImageUri()) +
                    ",," + digitalToASCII(224) + "," + digitalToASCII(756));

            temp.append("imgParam" + digitalToASCII(i) + "=" + digitalToASCII(mediaList.get(i-1).getTime()));
            //Log.d("getMediaName", getMediaName(mediaList.get(i-1).getImageUri()));

        }
        Log.d("temp.length", temp.toString().length() + "");
        return temp.toString();
    }

    public static String getMediaName(Uri imageUri) {
        return imageUri.toString().substring(imageUri.toString().lastIndexOf("/")+1);
    }

    //数字按位转成ASCII码值字符串
    public static String digitalToASCII(int digital) {

        StringBuilder temp = new StringBuilder();
        byte[] a = (digital + "").getBytes();
        for(byte b : a) {
            temp.append(b);
        }

        return temp.toString();
    }
}
