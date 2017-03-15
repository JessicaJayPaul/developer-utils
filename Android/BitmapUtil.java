import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;

/**
 * Created by caojiantao1 on 2017/3/8.
 *
 */
public class BitmapUtil {

    public static Bitmap getPropertyBitmap(InputStream is, int reqWidth, int reqHeight){
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 设置只解析宽高，防止OOM
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(is, null, options);
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
        bitmap = BitmapFactory.decodeStream(is, null, options);
        return  bitmap;
    }
}