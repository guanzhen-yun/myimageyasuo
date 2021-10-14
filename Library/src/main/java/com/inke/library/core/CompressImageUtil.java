package com.inke.library.core;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.widget.Toast;

import com.inke.library.config.CompressConfig;
import com.inke.library.listener.CompressResultListener;
import com.inke.library.utils.Constants;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        decodeFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length > config.getMaxSize()) {//循环判断如果压缩后图片是否大于100kb，大于继续压缩
            baos.reset();// 重置baos即清空baos
            decodeFile.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%,把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        File file = saveImgFile(context, bitmap, "compressPng");
        mHhHandler.post(new Runnable() {
            @Override
            public void run() {
                listener.onCompressSuccess(file.getAbsolutePath());
            }
        });
    }

    /**
     * 像素压缩
     */
    private void compressImageByPiexl(String imgPath, CompressResultListener listener) throws FileNotFoundException{
        Bitmap image = BitmapFactory.decodeFile(imgPath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        if(baos.toByteArray().length / 1024 > 1024) {//判断如果图片大于1M，进行压缩避免在生成图片 （BitmapFactory.decodeStream）时OOM
            baos.reset();
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*400分辨率，所以高和宽我们设置为
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f;
        //缩放比，由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if(w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if(w < h && h > hh) {//如果高度高的话根据高度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if(be <= 0) {
            be = 1;
        }
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.toByteArray().length, newOpts);
        compressImageByQuality(bitmap, imgPath, listener);
    }

    public static File saveImgFile(Context context, Bitmap bitmap, String fileName) {
        if (fileName == null) {
            System.out.println("saved fileName can not be null");
            return null;
        }
        File appDir = new File(context.getExternalCacheDir(), Constants.COMPRESS_CACHE);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        File file = new File(appDir, fileName);
        try {
            if(!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(file.getAbsolutePath())));
        return file;
    }
}
