import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class BitmapUtil {

    /**
     * 直接decodeStream，第二次会返回null，因为is已经改变，所以采用decodeByteArray的方式
     */
    public static Bitmap getPropertyBitmap(InputStream is, int reqWidth, int reqHeight){
        return  getPropertyBitmap(HttpUtil.getBytes(is), reqWidth, reqHeight);
    }

    public static Bitmap getPropertyBitmap(byte[] bytes, int reqWidth, int reqHeight){
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 设置只解析宽高，防止OOM
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        // 计算缩放的比例（double）
        double realWidth = options.outWidth;
        double realHeight = options.outHeight;
        double widthRate = realWidth / reqWidth;
        double heightRate = realHeight / reqHeight;
        double rate = widthRate > heightRate ? widthRate : heightRate;
        // 重新解析图片，得到合适的bitmap
        int inSampleSize = (int) Math.ceil(rate);
        options.inJustDecodeBounds = false;
        options.inSampleSize = inSampleSize;
        bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        return  bitmap;
    }
}
