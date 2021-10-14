package com.inke.myimageyasuo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.inke.library.CompressImageManager;
import com.inke.library.bean.Photo;
import com.inke.library.config.CompressConfig;
import com.inke.library.listener.CompressImage;
import com.inke.library.utils.CachePathUtils;
import com.inke.library.utils.CommonUtils;
import com.inke.library.utils.Constants;
import com.inke.myimageyasuo.utils.UriParseUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

public class MainActivity extends AppCompatActivity implements CompressImage.CompressListener {

    private CompressConfig compressConfig;// 压缩配置
    private ProgressDialog dialog;// 压缩加载框
    private String cameraCachePath; //拍照源文件路径

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //运行时权限申请
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] perms = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (checkSelfPermission(perms[0]) == PackageManager.PERMISSION_DENIED || checkSelfPermission(perms[1]) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(perms, 200);
            }
        }

        compressConfig = CompressConfig.builder()
                .setUnCompressMinPixel(1000) //最小像素不压缩，默认值：1000
                .setUnCompressNormalPixel(2000) //标准像素不压缩，默认值：2000
                .setMaxPixel(1200) //长或宽不超过的最大像素(单位px)，默认值：1200
                .setMaxSize(200 * 1024) //压缩到的最大大小(单位B)，默认值：200 * 1024 = 200KB
                .setEnablePixelCompress(true) //是否启用像素压缩，默认值：true
                .setEnableQualityCompress(true) //是否启用质量压缩，默认值：true
                .setEnableReserveRaw(true) //是否保留源文件，默认值：true
                .setCacheDir("") //压缩后缓存图片路径，默认值: Constants.COMPRESS_CACHE
                .setShowCompressDialog(true) //是否显示压缩进度条，默认值：true
                .create();

        // compressConfig = CompressConfig.getDefaultConfig();

//        testLuban();
//        compressMore();
    }

    //测试多张图片同时压缩
    private void compressMore() {
        ArrayList<Photo> photos = new ArrayList<>();
        photos.add(new Photo(""));
        photos.add(new Photo(""));
        photos.add(new Photo(""));
        compress(photos);
    }

    //测试鲁班压缩
    private void testLuban() {
        String mCacheDir = Constants.BASE_CACHE_PATH + getPackageName() + "/cache/" + Constants.COMPRESS_CACHE;
        Luban.with(this)
                .load("/storage/emulated/0/DCIM/Camera/IMG_20190322_142010.jpg")
                .ignoreBy(100) //忽略100KB不压缩
                .setTargetDir(mCacheDir)
                .filter(new CompressionPredicate() {
                    @Override
                    public boolean apply(String path) {
                        return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"));
                    }
                })
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                        Log.e("onStart", "start");
                    }

                    @Override
                    public void onSuccess(File file) {
                        Log.e("onSuccess", file.getAbsolutePath());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("onError", e.toString());
                    }
                }).launch();
    }

    @SuppressLint("CheckResult")
    private void initLuBanRxJava(String path) {
        String mCacheDir = Constants.BASE_CACHE_PATH + getPackageName() + "/cache/" + Constants.COMPRESS_CACHE;
        final List<String> photos = new ArrayList<>();
        photos.add(path);
        photos.add(path);
        photos.add(path);
        //背压
        Flowable.just(photos)//注意，可以单个压缩，也可可以List压缩
                .subscribeOn(Schedulers.io())
                .map(new Function<List<String>, List<File>>() {
                    @Override
                    public List<File> apply(@NonNull List<String> strings) throws Exception{
                        /**
                         * 如果需要保存到本地就使用setTargetDir方法
                         */
                        return Luban.with(MainActivity.this).load(photos).setTargetDir(mCacheDir).get();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<File>>() {
                    @Override
                    public void accept(List<File> files) throws Exception {
                        if(!files.isEmpty()) {
                            for (File file : files) {
                                Log.e("netease >>> ", file.getAbsolutePath());
                            }
                        }
                    }
                });
    }

    //点击拍照
    public void camera(View view) {
        //Android 7.0 File 路径的变更，需要使用FileProvider 来做
        Uri outputUri;
        File file = CachePathUtils.getCameraCacheFile();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            outputUri = UriParseUtils.getCameraOutPutUri(this, file);
        } else {
            outputUri = Uri.fromFile(file);
        }
        cameraCachePath = file.getAbsolutePath();
        //启动拍照
        CommonUtils.hasCamera(this, CommonUtils.getCameraIntent(outputUri), Constants.CAMERA_CODE);
    }

    //点击相册
    public void album(View view) {
        CommonUtils.openAlbum(this, Constants.ALBUM_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //拍照返回
        if (requestCode == Constants.CAMERA_CODE && resultCode == RESULT_OK) {
            //压缩 （集合？单张）
            preCompress(cameraCachePath);
        }

        //相册返回
        if (requestCode == Constants.ALBUM_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                String path = UriParseUtils.getPath(this, uri);
                //压缩（集合？单张）
                preCompress(path);
            }
        }
    }

    //准备压缩，封装图片集合
    private void preCompress(String photoPath) {
        ArrayList<Photo> photos = new ArrayList<>();
        photos.add(new Photo(photoPath));
        if (!photos.isEmpty()) compress(photos);
    }

    //开始压缩
    private void compress(ArrayList<Photo> photos) {
        if(compressConfig.isShowCompressDialog()) {
            dialog = CommonUtils.showProgressDialog(this, "图片压缩中....");
        }
        //收集需要压缩的图片集合后，开启压缩动作
        CompressImageManager.build(this, compressConfig, photos, this).compress();
    }

    @Override
    public void onCompressSuccess(ArrayList<Photo> images) {
        Log.e("netease >>> ", "压缩成功");
        if(dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    public void onCompressFailed(ArrayList<Photo> images, String... error) {
        for (String s : error) {
            Log.e("netease >>> ", s);
        }
        if(dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}