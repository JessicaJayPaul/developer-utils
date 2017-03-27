import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 磁盘缓存工具类
 */

public class DiskLruCacheUtil {

    private static final int VALUE_COUNT = 1;

    private static final long CACHE_SIZE = 10 * 1024 * 1024;

    private static final String DIRECTORY_NAME = "bitmap";

    private DiskLruCache diskLruCache;

    private DiskLruCacheUtil util;

    private DiskLruCacheUtil(Context context){
        try {
            diskLruCache = DiskLruCache.open(getDiskCacheDir(context, DIRECTORY_NAME), getAppVersion(context), VALUE_COUNT, CACHE_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 懒汉式加载，单例模式
     */
    public DiskLruCacheUtil with(Context context){
        if (util == null){
            util = new DiskLruCacheUtil(context);
        }
        return util;
    }

    /**
     * 添加网络图片至磁盘缓存
     */
    public void addNetImgToDiskLruCache(String url){
        String key = getMD5String(url);
        InputStream is = (InputStream) HttpUtil.getInstance().connect(url)
                .stream()
                .get();
        addInputStreamToDiskLruCache(key, is);
    }

    /**
     * 通过url从磁盘缓存中获取InputStream
     */
    public InputStream getInputStreamFromDiskLruCacheByUrl(String url){
        InputStream is = null;
        if (diskLruCache != null){
            String key = getMD5String(url);
            try {
                DiskLruCache.Snapshot snapShot = diskLruCache.get(key);
                if (snapShot != null){
                    is = snapShot.getInputStream(0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return is;
    }

    public void addInputStreamToDiskLruCache(String key, InputStream is){
        if (diskLruCache == null){
            return;
        }
        try {
            DiskLruCache.Editor editor = diskLruCache.edit(key);
            OutputStream os = editor.newOutputStream(0);
            int len;
            byte[] buffer = new byte[1024];
            while ((len = is.read(buffer)) != -1){
                os.write(buffer, 0, len);
            }
            editor.commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取磁盘缓存文件
     */
    public static File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            // 存在sd卡
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    /**
     * 获取应用版本号
     */
    public int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    /**
     * MD5加密字符串
     */
    public static String getMD5String(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes){
            String hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}
