package com.inke.library.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;

import com.inke.library.config.CompressConfig;
import com.inke.library.listener.CompressResultListener;

import java.io.FileNotFoundException;

/**
 * 压缩图片
 */
public class CompressImageUtil {

    private CompressConfig config;
    private Context context;
    private Handler mHhHandler = new Handler(Looper.getMainLooper());

    public CompressImageUtil(Context context, CompressConfig config) {
        this.context = context;
        this.config = config;
    }

    public void compress(String imgPath, CompressResultListener listener) {
        if(config.isEnablePixelCompress()) {
             try {
                 compressImageByPiexl(imgPath, listener);
             } catch (FileNotFoundException e) {
                 listener.onCompressFailed(imgPath, String.format("图片压缩失败,%s", e.toString()));
                 e.printStackTrace();
             }
        } else {
            compressImageByQuality(BitmapFactory.decodeFile(imgPath), imgPath, listener);
        }
    }

    /**
     * 多线程压缩图片的质量
     */
    private void compressImageByQuality(Bitmap decodeFile, String imgPath, final CompressResultListener listener) {

    }

    /**
     * 像素压缩
     */
    private void compressImageByPiexl(String imgPath, CompressResultListener listener) throws FileNotFoundException{

    }
}
