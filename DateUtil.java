import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    /**
     * ��
     */
    public static final long SECOND = 1000;

    /**
     * ��
     */
    public static final long MINUTE = SECOND * 60;

    /**
     * Сʱ
     */
    public static final long HOUR = MINUTE * 60;

    /**
     * ��
     */
    public static final long DAY = HOUR * 24;
    
    /**
     * �������ڣ��õ��ַ�����Ĭ�ϸ�ʽΪyyyy-MM-dd
     */
    public static String parseDate(Date date){
        return parseDate(date, "yyyy-MM-dd");
    }

    /**
     * �������ڣ��õ��ַ���
     */
    public static String parseDate(Date date, String format) {
        return new SimpleDateFormat(format).format(date);
    }
    
    /**
     * �����ַ������õ����ڣ�Ĭ�ϸ�ʽΪyyyy-MM-dd
     */
    public static Date parseStr(String str){
        return parseStr(str, "yyyy-MM-dd");
    }
    
    /**
     * �����ַ������õ�����
     */
    public static Date parseStr(String dateStr, String format){
        Date date = null;
        try {
            date = new SimpleDateFormat(format).parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * ��ȡ���ڣ�Ĭ�ϸ�ʽΪyyyy-MM-dd
     */
    public static String getWeek(String dateStr) {
        return getWeek(dateStr, "yyyy-MM-dd");
    }
    
    public static String getWeek(String dateStr, String format) {
        Date date = parseStr(dateStr, format);
        return getWeek(date);
    }

    /**
     * ��ȡ����
     */
    public static String getWeek(Date date) {
        String[] weekOfDays = { "������", "����һ", "���ڶ�", "������", "������", "������", "������" };
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int w = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        return weekOfDays[w];
    }

    /**
     * ����ָ��ʱ��
     * @param type SECOND�룻MINUTE�֣�HOURСʱ��DAY�죻
     */
    public static Date addTime(Date date, int duration, long type) {
        long longTime = duration * type;
        return new Date(date.getTime() + longTime);
    }
    
    public static double getDuration(String firstDateStr, String secondDateStr, long type){
        return getDuration(firstDateStr, secondDateStr, type, "yyyy-MM-dd HH:mm:ss");
    }

    public static double getDuration(String firstDateStr, String secondDateStr, long type, String format){
        return getDuration(firstDateStr, secondDateStr, type, format, 2);
    }
    
    public static double getDuration(String firstDateStr, String secondDateStr, long type, int accuracy){
        return getDuration(firstDateStr, secondDateStr, type, "yyyy-MM-dd HH:mm:ss", accuracy);
    }

    public static double getDuration(String firstDateStr, String secondDateStr, long type, String format, int accuracy){
        Date firstDate = parseStr(firstDateStr, format);
        Date secondDate = parseStr(secondDateStr, format);
        return getDuration(firstDate, secondDate, type, accuracy);
    }

    public static double getDuration(Date firstDate, Date secondDate, long type){
        return getDuration(firstDate, secondDate, type, 2);
    }
    
    /**
     * �õ�����ʱ���������Ϳɹ�ѡ���������룬Ĭ�Ͼ�ȷ����λС����
     * @param type SECOND�룻MINUTE�֣�HOURСʱ��DAY�죻
     */
    public static double getDuration(Date firstDate, Date secondDate, long type, int accuracy) {
        double longTime = (secondDate.getTime() - firstDate.getTime()) * 1.0;
        double value = longTime / type;
        BigDecimal b = new BigDecimal(value);
        return b.setScale(accuracy, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
    
    public static boolean isLeapYear(String year){
        return isLeapYear(Integer.parseInt(year));
    }
    
    /**
     * �ж��Ƿ�Ϊ����
     */
    public static boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }
}
