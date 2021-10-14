package com.inke.myimageyasuo.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;

import com.inke.myimageyasuo.MainActivity;

import java.io.File;

public class UriParseUtils {

    /**
     * 创建一个图片文件输出路径的Uri (FileProvider)
     *
     * @param context 上下文
     * @param file    文件
     * @return 转换后的Scheme为FileProvider的Uri
     */
    private static Uri getUriForFile(Context context, File file) {
        return FileProvider.getUriForFile(context, getFileProvider(context), file);
    }

    /**
     * 获取FileProvider路径，适配6.0+
     *
     * @param context 上下文
     * @return FileProvider路径
     */
    private static String getFileProvider(Context context) {
        return context.getPackageName() + ".fileprovider";
    }


    public static Uri getCameraOutPutUri(Context context, File file) {
        return getUriForFile(context, file);
    }

    public static String getPath(Context context, Uri uri) {
        String path = "";
        if (Build.VERSION.SDK_INT >= 19) {
            // 4.4及以上系统使用这个方法处理图片
            if (DocumentsContract.isDocumentUri(context, uri)) {
                // 如果是document类型的Uri，则通过document id处理
                String docId = DocumentsContract.getDocumentId(uri);
                if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                    String id = docId.split(":")[1];
                    // 解析出数字格式的id
                    String selection = MediaStore.Images.Media._ID + "=" + id;
                    path = getImagePath(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
                } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                    Uri contentUri = ContentUris.withAppendedId(Uri.parse("content: //downloads/public_downloads"), Long.valueOf(docId));
                    path = getImagePath(context, contentUri, null);
                }
            } else if ("content".equalsIgnoreCase(uri.getScheme())) {
                // 如果是content类型的Uri，则使用普通方式处理
                path = getImagePath(context, uri, null);
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                // 如果是file类型的Uri，直接获取图片路径即可
                path = uri.getPath();
            }
        } else {
            path = getImagePath(context, uri, null);
        }
        return path;
    }

    private static String getImagePath(Context context, Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = context.getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

}
