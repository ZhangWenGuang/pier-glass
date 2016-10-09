package com.qstech.pierglass.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.qstech.pierglass.App;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by admin on 2016/9/26.
 */

public class BitmapUtils {

    //获取到的bitmap大小限制
    static int width = 224;
    static int height = 756;

    /**
     * 通过id获取内存占用少的bitmap
     *
     * @param resId 资源id
     * @return bitmap
     */
    public static Bitmap getSmallBitmap(int resId) {
        //获取资源文件的属性信息
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;//不将图片像素直接加入内存，只提取参数
        BitmapFactory.decodeResource(App.getInstance().getResources(), resId, options);
        // 获取inSampleSize
        options.inSampleSize = calculateInSampleSize(options, width, height);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(App.getInstance().getResources(), resId, options);
    }

    /**
     * 通过Uri获取内存占用少的bitmap
     *
     * @param imageUri
     * @return bitmap
     */
    public static Bitmap getSmallBitmap(Uri imageUri) {
        //BitmapFactory只能通过id或者filePath创建bitmap，所以需要将Uri转为filePath
        String filePath = getRealFilePath(App.getInstance(), imageUri);
        return getSmallBitmap(filePath);
    }

    /**
     * 通过filePath路径获取内存占用少的bitmap
     *
     * @param filePath
     * @return bitmap
     */
    public static Bitmap getSmallBitmap(String filePath) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, width, height);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    /**
     * 计算符合宽高限制的图片的缩放值
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return 缩放值
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        final int height = options.outHeight;
        final int width = options.outWidth;

        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    /**
     * 判断图片是否符合一定的宽高比
     *
     * @param imageUri
     * @return boolean
     */
    public static boolean isQualifiedBitmap(Uri imageUri) {

        String path = getRealFilePath(App.getInstance(), imageUri);

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        final int height = options.outHeight;
        final int width = options.outWidth;
        if (width != 0) {
            float tmp = (float) height / width;
            Log.d("BtmU.isQBtm", "h:w=" + tmp);
            //比例为756:224时，值为3.375
            if (tmp >= 3.36 && tmp < 3.4) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 通过Uri获取文件的filePath
     *
     * @param context
     * @param uri
     * @return filePath
     */
    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    /**
     * 保存bitmap图片到本地（文件名为当前系统时间）
     *
     * @param bmp bitmap对象
     * @param dir 本地文件夹路径
     */
    public static void saveImage(Bitmap bmp, File dir) {
        String fileName = System.currentTimeMillis() + ".jpg";
        saveImage(bmp, dir, fileName);
    }

    /**
     * 保存bitmap图片到本地
     *
     * @param bmp      bitmap对象
     * @param dir      本地文件夹路径
     * @param fileName 文件名
     */
    public static void saveImage(Bitmap bmp, File dir, String fileName) {
        File appDir = dir;
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
