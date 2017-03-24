package com.example.caojiantao1.myapplication;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * 内存缓存工具类（内存管理，自动回收）
 */
public class BitmapLruCacheUtil {

    private static LruCache<String, Bitmap> lruCache;

    /**
     * 恶汉式加载，程序启动就会初始化LruCache
     */
    private static BitmapLruCacheUtil util = new BitmapLruCacheUtil();

    private BitmapLruCacheUtil(){
        // 获取应用程序最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        // 设置图片缓存大小为程序最大可用内存的1/8
        lruCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // 获取每张图片的大小，要注意和cacheSize单位一致
                return bitmap.getByteCount();
            }
        };
    }

    /**
     * 单例模式，保证程序LruCache唯一
     */
    public static BitmapLruCacheUtil getInstance(){
        return util;
    }

    /**
     * 从LruCache中取bitmap
     */
    public Bitmap getBitmapFromLruCache(String key) {
        return lruCache.get(key);
    }

    /**
     * 添加bitmap至LruCache
     */
    public void addBitmapToLruCache(String key, Bitmap bitmap) {
        if (getBitmapFromLruCache(key) == null) {
            lruCache.put(key, bitmap);
        }
    }
}
