package com.inke.library;

import android.content.Context;
import android.text.TextUtils;

import com.inke.library.bean.Photo;
import com.inke.library.config.CompressConfig;
import com.inke.library.core.CompressImageUtil;
import com.inke.library.listener.CompressImage;
import com.inke.library.listener.CompressResultListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 图片压缩管理类(接口的实现类)
 */
public class CompressImageManager implements CompressImage {

    private CompressImageUtil compressImageUtil; //图片压缩的工具类
    private ArrayList<Photo> images;// 需要压缩的图片集合
    private CompressImage.CompressListener listener;// 压缩监听，通知MainActivity
    private CompressConfig config; //压缩的配置

    private CompressImageManager(Context context, CompressConfig config, ArrayList<Photo> images, CompressListener listener) {
        compressImageUtil = new CompressImageUtil(context, config);
        this.config = config;
        this.images = images;
        this.listener = listener;
    }

    // 接口 = 接口实现类
    public static CompressImage build(Context context, CompressConfig config,
                                      ArrayList<Photo> images, CompressImage.CompressListener listener) {
        return new CompressImageManager(context, config, images, listener);
    }

    @Override
    public void compress() {
        if (images == null || images.isEmpty()) {
            listener.onCompressFailed(images, "图片的集合为空");
            return;
        }

        for (Photo image : images) {
            if (image == null) {
                listener.onCompressFailed(images, "图片的集合中有个别为空");
                return;
            }
        }

        //开始压缩，从第一张开始，index = 0
        compress(images.get(0));
    }

    //做单张图片压缩前，需要做图片校验
    private void compress(Photo image) {
        if (TextUtils.isEmpty(image.getOriginalPath())) {
            // 继续压缩
            continueCompress(image, false);
            return;
        }

        //图片不存在，或者不是一个文件
        File file = new File(image.getOriginalPath());
        if (!file.isFile() || !file.exists()) {
            //继续压缩
            continueCompress(image, false);
            return;
        }

        //90KB < 200KB不压缩
        if (file.length() < config.getMaxSize()) {
            continueCompress(image, true);
            return;
        }

        //校验通过，开始使用工具类压缩了
        compressImageUtil.compress(image.getOriginalPath(), new CompressResultListener() {
            @Override
            public void onCompressSuccess(String imgPath) {
                //压缩成功
                image.setCompressPath(imgPath);
                continueCompress(image, true);
            }

            @Override
            public void onCompressFailed(String imgPath, String error) {
                continueCompress(image, false, error);
            }
        });
    }

    private void continueCompress(Photo image, boolean isCompressed, String... error) {
        image.setCompressed(isCompressed);
        //获取当前的压缩图片的索引
        int index = images.indexOf(image);
        //如果不是集合中最好一张图片，则进入递归继续压缩
        if(index == images.size() - 1) {
            // 通知UI
            handlerCallback(error);
        } else {
            //递归
            compress(images.get(index + 1));
        }
    }

    private void handlerCallback(String... error) {
        if(error.length > 0) {
            listener.onCompressFailed(images, error);
            return;
        }

        for (Photo image : images) {
            if(!image.isCompressed()) {
                listener.onCompressFailed(images, image.getOriginalPath() + "图片压缩失败");
                return;
            }
        }

        listener.onCompressSuccess(images);
    }

    public List<File> get() {
        List<File> list = new ArrayList<>();
        for (Photo image : images) {
            if(image.isCompressed() && !TextUtils.isEmpty(image.getCompressPath())) {
                File file = new File(image.getOriginalPath());
                list.add(file);
            }
        }
        return list.isEmpty() ? null : list;
    }
}
