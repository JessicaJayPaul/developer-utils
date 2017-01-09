import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
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
        HttpURLConnection connection = null;
        // 使用StringBuilder避免在while循环体中多次创建String对象而浪费资源
        StringBuilder builder = new StringBuilder();
        try {
            if (map != null && !map.isEmpty()) {
                url = url + "?" + getParamter(map);
            }
            connection = (HttpURLConnection) new URL(url).openConnection();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), DEFAULT_ENCODE));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
    				builder.append(System.getProperty(LINE_SEPARATOR));
                }
                reader.close();
            }
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
        HttpURLConnection connection = null;
        StringBuilder builder = new StringBuilder();
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            // post请求必须设置此项
            connection.setDoOutput(true);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), DEFAULT_ENCODE));
            writer.write(getParamter(map));
            writer.flush();
            writer.close();
            System.out.println(connection.getResponseCode());
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // 获取返回数据
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), DEFAULT_ENCODE));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                    builder.append(System.getProperty(LINE_SEPARATOR));
                }
                reader.close();
            }
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
     * 将封装的map参数转换为String字符串，以便写入输出流中
     */
    public static String getParamter(Map<String, String> map){
        if (map == null || map.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (Entry<String, String> entry : map.entrySet()) {
            builder.append(entry.getKey());
            builder.append("=");
            builder.append(entry.getValue());
            builder.append("&");
        }
        // 去掉最后一个&
        return builder.substring(0, builder.length() - 1);
    }
    
    public static void main(String[] args) {
    	Map<String, String> map = new HashMap<String, String>();
    	map.put("name", "wulitao");
    	System.out.print(getParamter(map));
	}
}