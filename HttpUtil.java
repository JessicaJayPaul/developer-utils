import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

public class HttpUtil {
    
    public static final String DEFAULT_ENCODE = "UTF-8";
    
    public static final String LINE_SEPARATOR = "line.separator";

    /**
     * 发起get请求，获取返回文本信息
     */
    public static String doGet(String url){
        return doGet(url, null);
    }
    
    public static String doGet(String url, Map<String, String> map){
        return doGet(url, map, null);
    }
    
    public static String doGet(String url, Map<String, String> map, Map<String, String> propertyMap){
        HttpURLConnection connection = null;
        // 使用StringBuilder避免在while循环体中多次创建String对象而浪费资源
        StringBuilder builder = new StringBuilder();
        try {
            if (map != null && !map.isEmpty()) {
                url = url + "?" + getParamter(map);
            }
            connection = (HttpURLConnection) new URL(url).openConnection();
            setRequestProperty(connection, propertyMap);
            readData(connection, builder);
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            if (connection != null) {
                connection.disconnect();
            }
        }
        return builder.toString();
    }
    
    /**
     * 发起post请求，获取返回文本信息
     */
    public static String doPost(String url, Map<String, String> map){
        return doPost(url, map, null);
    }
    
    public static String doPost(String url, Map<String, String> map, Map<String, String> propertyMap){
        HttpURLConnection connection = null;
        StringBuilder builder = new StringBuilder();
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("POST");
            // post请求必须设置此项
            connection.setDoOutput(true);
            setRequestProperty(connection, propertyMap);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), DEFAULT_ENCODE));
            writer.write(getParamter(map));
            writer.flush();
            writer.close();
            readData(connection, builder);
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            if (connection != null) {
                connection.disconnect();
            }
        }
        return builder.toString();
    }
    
    /**
     * 设置请求参数
     */
    public static void setRequestProperty(HttpURLConnection connection, Map<String, String> map){
        if (map == null || map.isEmpty()) {
            return;
        }
        for (Entry<String, String> entry : map.entrySet()) {
            connection.setRequestProperty(entry.getKey(), entry.getValue());
        }
    }
    
    /**
     * 将封装的map参数转换为字符串，以便写入输出流中
     */
    public static String getParamter(Map<String, String> map) throws UnsupportedEncodingException{
        if (map == null || map.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        // encode下字符串，避免参数含有特殊字符
        for (Entry<String, String> entry : map.entrySet()) {
            builder.append(URLEncoder.encode(entry.getKey(), DEFAULT_ENCODE));
            builder.append("=");
            builder.append(URLEncoder.encode(entry.getValue(), DEFAULT_ENCODE));
            builder.append("&");
        }
        // 去掉最后一个&
        return builder.substring(0, builder.length() - 1);
    }
    
    public static void readData(HttpURLConnection connection, StringBuilder builder) throws UnsupportedEncodingException, IOException{
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), DEFAULT_ENCODE));
            String line = null;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty(LINE_SEPARATOR));
            }
            reader.close();
        }
    }
}