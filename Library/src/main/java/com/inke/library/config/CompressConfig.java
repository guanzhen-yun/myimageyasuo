package com.inke.library.config;

import java.io.Serializable;

/**
 * 压缩配置类
 */
public class CompressConfig implements Serializable {
    /**
     * 最小像素不压缩
     */
    private int unCompressMinPixel = 1000;
    /**
     * 标准像素不压缩
     */
    private int unCompressNormalPixel = 2000;
    /**
     * 长或宽不超过的最大像素，单位px
     */
    private int maxPixel = 1200;
    /**
     * 压缩到的最大大小，单位B
     */
    private int maxSize = 200 * 1024;
    /**
     * 是否启用像素压缩
     */
    private boolean enablePixelCompress = true;
    /**
     * 是否启用质量压缩
     */
    private boolean enableQualityCompress = true;
    /**
     * 是否保留源文件
     */
    private boolean enableReserveRaw = true;

    /**
     * 压缩后缓存图片目录，非文件路径
     */
    private String cacheDir;
    /**
     * 是否显示压缩进度条
     */
    private boolean showCompressDialog;

    public CompressConfig(CompressConfigBuilder compressConfigBuilder) {
        setCacheDir(compressConfigBuilder.getCacheDir());
        setEnablePixelCompress(compressConfigBuilder.isEnablePixelCompress());
        setEnableQualityCompress(compressConfigBuilder.isEnableQualityCompress());
        setEnableReserveRaw(compressConfigBuilder.isEnableReserveRaw());
        setMaxPixel(compressConfigBuilder.getMaxPixel());
        setShowCompressDialog(compressConfigBuilder.isShowCompressDialog());
        setUnCompressMinPixel(compressConfigBuilder.getUnCompressMinPixel());
        setUnCompressNormalPixel(compressConfigBuilder.getUnCompressNormalPixel());
        setMaxSize(compressConfigBuilder.getMaxSize());
    }

    public static CompressConfig getDefaultConfig() {
        return new CompressConfig();
    }

    private CompressConfig() {
    }

    public static CompressConfigBuilder builder() {
        return new CompressConfigBuilder();
    }

    public static class CompressConfigBuilder {
        /**
         * 最小像素不压缩
         */
        private int unCompressMinPixel = 1000;
        /**
         * 标准像素不压缩
         */
        private int unCompressNormalPixel = 2000;
        /**
         * 长或宽不超过的最大像素，单位px
         */
        private int maxPixel = 1200;
        /**
         * 压缩到的最大大小，单位B
         */
        private int maxSize = 200 * 1024;
        /**
         * 是否启用像素压缩
         */
        private boolean enablePixelCompress = true;
        /**
         * 是否启用质量压缩
         */
        private boolean enableQualityCompress = true;
        /**
         * 是否保留源文件
         */
        private boolean enableReserveRaw = true;

        /**
         * 压缩后缓存图片目录，非文件路径
         */
        private String cacheDir;
        /**
         * 是否显示压缩进度条
         */
        private boolean showCompressDialog;

        public int getUnCompressMinPixel() {
            return unCompressMinPixel;
        }

        public CompressConfigBuilder setUnCompressMinPixel(int unCompressMinPixel) {
            this.unCompressMinPixel = unCompressMinPixel;
            return this;
        }

        public int getUnCompressNormalPixel() {
            return unCompressNormalPixel;
        }

        public CompressConfigBuilder setUnCompressNormalPixel(int unCompressNormalPixel) {
            this.unCompressNormalPixel = unCompressNormalPixel;
            return this;
        }

        public int getMaxPixel() {
            return maxPixel;
        }

        public CompressConfigBuilder setMaxPixel(int maxPixel) {
            this.maxPixel = maxPixel;
            return this;
        }

        public int getMaxSize() {
            return maxSize;
        }

        public CompressConfigBuilder setMaxSize(int maxSize) {
            this.maxSize = maxSize;
            return this;
        }

        public boolean isEnablePixelCompress() {
            return enablePixelCompress;
        }

        public CompressConfigBuilder setEnablePixelCompress(boolean enablePixelCompress) {
            this.enablePixelCompress = enablePixelCompress;
            return this;
        }

        public boolean isEnableQualityCompress() {
            return enableQualityCompress;
        }

        public CompressConfigBuilder setEnableQualityCompress(boolean enableQualityCompress) {
            this.enableQualityCompress = enableQualityCompress;
            return this;
        }

        public boolean isEnableReserveRaw() {
            return enableReserveRaw;
        }

        public CompressConfigBuilder setEnableReserveRaw(boolean enableReserveRaw) {
            this.enableReserveRaw = enableReserveRaw;
            return this;
        }

        public String getCacheDir() {
            return cacheDir;
        }

        public CompressConfigBuilder setCacheDir(String cacheDir) {
            this.cacheDir = cacheDir;
            return this;
        }

        public boolean isShowCompressDialog() {
            return showCompressDialog;
        }

        public CompressConfigBuilder setShowCompressDialog(boolean showCompressDialog) {
            this.showCompressDialog = showCompressDialog;
            return this;
        }

        public CompressConfig create() {
            return new CompressConfig(this);
        }
    }

    public int getUnCompressMinPixel() {
        return unCompressMinPixel;
    }

    public void setUnCompressMinPixel(int unCompressMinPixel) {
        this.unCompressMinPixel = unCompressMinPixel;
    }

    public int getUnCompressNormalPixel() {
        return unCompressNormalPixel;
    }

    public void setUnCompressNormalPixel(int unCompressNormalPixel) {
        this.unCompressNormalPixel = unCompressNormalPixel;
    }

    public int getMaxPixel() {
        return maxPixel;
    }

    public void setMaxPixel(int maxPixel) {
        this.maxPixel = maxPixel;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public boolean isEnablePixelCompress() {
        return enablePixelCompress;
    }

    public void setEnablePixelCompress(boolean enablePixelCompress) {
        this.enablePixelCompress = enablePixelCompress;
    }

    public boolean isEnableQualityCompress() {
        return enableQualityCompress;
    }

    public void setEnableQualityCompress(boolean enableQualityCompress) {
        this.enableQualityCompress = enableQualityCompress;
    }

    public boolean isEnableReserveRaw() {
        return enableReserveRaw;
    }

    public void setEnableReserveRaw(boolean enableReserveRaw) {
        this.enableReserveRaw = enableReserveRaw;
    }

    public String getCacheDir() {
        return cacheDir;
    }

    public void setCacheDir(String cacheDir) {
        this.cacheDir = cacheDir;
    }

    public boolean isShowCompressDialog() {
        return showCompressDialog;
    }

    public void setShowCompressDialog(boolean showCompressDialog) {
        this.showCompressDialog = showCompressDialog;
    }
}
