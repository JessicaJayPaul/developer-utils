import java.math.BigDecimal;


public class NumberUtil {
    
    /**
     * 默认小数精度
     */
    public static final int ACCURACY = 2;
    
    /**
     * 获取2位小数
     */
    public static double getDvalue(double num){
    	return getDvalue(num, ACCURACY);
    }
    
    /**
     * 获取指定精度小数
     */
	public static double getDvalue(double num, int accuracy){
		BigDecimal b = new BigDecimal(num);
        return b.setScale(accuracy, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	public static void main(String[] args) {
		System.out.print(getDvalue(1.4452, 1));
	}
}
