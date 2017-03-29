import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

public class Hong {

    private static Hong hong = null;

    private Resources resources;

    private DiskLruCacheUtil diskLruCacheUtil;

    private Object imgRes;

    private int width;

    private int height;

    private Hong(){}

    /**
     * 单例设计模式，懒汉式加载
     */
    public static Hong with(Context context){
        if (hong == null){
            hong = new Hong();
            hong.resources = context.getResources();
            hong.diskLruCacheUtil = new DiskLruCacheUtil(context);
        }
        return hong;
    }

    public Hong load(String path){
        this.imgRes = path;
        return this;
    }

    public Hong load(int id){
        this.imgRes = id;
        return this;
    }

    public Hong resize(int width, int height){
        this.width = width;
        this.height = height;
        return this;
    }

    public void into(ImageView imageView){
        Bitmap bitmap;
        // 去项目资源取
        if (imgRes instanceof Integer){
            InputStream inputStream = resources.openRawResource(Integer.parseInt(imgRes.toString()));
            bitmap = getBitmap(inputStream);
            imageView.setImageBitmap(bitmap);
            return;
        }

        String path = imgRes.toString();
        File file = new File(path);
        // 去本地文件取
        if (!file.exists()){
            // 去内存缓存取
            bitmap = BitmapLruCacheUtil.getInstance().getBitmapFromLruCache(path);
            if (bitmap == null){
                // 去磁盘缓存取
                InputStream is = diskLruCacheUtil.getInputStreamFromDiskLruCacheByUrl(path);
                if (is == null){
                    // 发起http请求，获取网络资源（同时添加内存缓存、磁盘缓存中）
                    new ImageCacheTask(path, imageView).execute();
                } else {
                    bitmap = getBitmap(is);
                    // 磁盘缓存已存在，添加到内存缓存中
                    BitmapLruCacheUtil.getInstance().addBitmapToLruCache(path, bitmap);
                }
            }
        } else{
            bitmap = getBitmap(file);
        }
        imageView.setImageBitmap(bitmap);
    }

    private Bitmap getBitmap(InputStream is){
        Bitmap bitmap;
        if (width == 0 || height == 0){
            bitmap = BitmapUtil.getProperBitmap(is);
        } else {
            bitmap = BitmapUtil.getProperBitmap(is, width, height);
        }
        return bitmap;
    }

    private Bitmap getBitmap(File file){
        Bitmap bitmap;
        if (width == 0 || height == 0){
            bitmap = BitmapUtil.getProperBitmap(file);
        } else {
            bitmap = BitmapUtil.getProperBitmap(file, width, height);
        }
        return bitmap;
    }

    /**
     * 加载网络图片资源任务类
     */
    private class ImageCacheTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;

        private ImageView imageView;

        private ImageCacheTask(String url, ImageView imageView){
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            Object response = HttpUtil.getInstance().connect(url)
                    .byteArray()
                    .get();
            if (response instanceof Exception){
                // 获取网络资源异常
                return null;
            }
            byte[] bytes = (byte[]) response;
            Bitmap bitmap = BitmapUtil.getProperBitmap(bytes);
            // 写入内存缓存
            BitmapLruCacheUtil.getInstance().addBitmapToLruCache(url, bitmap);
            // 写入磁盘缓存
            InputStream is = new ByteArrayInputStream(bytes);
            diskLruCacheUtil.addInputStreamToDiskLruCache(url, is);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            // 主线程更新ImageView
            imageView.setImageBitmap(bitmap);
        }
    }
}
