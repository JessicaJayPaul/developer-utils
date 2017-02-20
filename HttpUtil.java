import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

/**
 * HTTP工具类
 * @author wulitao
 * @date 2017年2月19日
 * @subscription
 */
public class HttpUtil {
    
    /**
     * 默认格式化编码格式
     */
    public static final String DEFAULT_ENCODE = "UTF-8";
    
    /**
     * 系统换行符
     */
    public static final String LINE_SEPARATOR = "line.separator";
    
    /**
     * http连接信息
     */
    private HttpURLConnection connection;
    
    /**
     * 请求回调接口（用于获取响应头，提取保存的cookie信息）
     */
    private Callback callback;
    
    /**
     * 请求参数
     */
    private String data;
    
    /**
     * 私有构造方法
     */
    private HttpUtil(){}
    
    /**
     * 实现类
     * @return
     */
    public static HttpUtil getInstance(){
        return new HttpUtil();
    }
    
    /**
     * 初始化访问地址url
     * @param url
     * @return
     */
    public HttpUtil connect(String url){
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }
    
    /**
     * 设置请求参数，注意格式化编码
     * @param key
     * @param value
     * @return
     */
    public HttpUtil data(String key, String value){
        String curData = encodeString(key) + "=" + encodeString(value);
        if (data == null || data.equals("")) {
            data = curData;
        } else {
            data += "&" + curData;
        }
        return this;
    }
    
    /**
     * 设置请求参数，类型为map
     * @param map
     * @return
     */
    public HttpUtil data(Map<String, String> map) {
        data = getParamter(map);
        return this;
    }
    
    /**
     * 设置请求头部
     * @param key
     * @param value
     * @return
     */
    public HttpUtil header(String key, String value){
        connection.addRequestProperty(key, value);
        return this;
    }
    
    /**
     * 设置请求cookie
     * @param key
     * @param value
     * @return
     */
    public HttpUtil cookie(String key, String value){
        connection.addRequestProperty("Cookie", key + "=" + value);
        return this;
    }

    /**
     * 设置请求响应接口
     * @param callback
     * @return
     */
    public HttpUtil callBack(Callback callback){
        this.callback = callback;
        return this;
    }
    
    /**
     * 发起get请求，返回响应数据（String）
     * @return
     */
    public String get(){
        return getResponseStr("GET");
    }
    
    /**
     * 发起post请求，返回相应数据（String）
     * @return
     */
    public String post(){
        return getResponseStr("POST");
    }
    
    /**
     * 发起http访问获取响应数据
     * @param method
     * @return
     */
    public String getResponseStr(String method){
        // 采用builder，
        StringBuilder builder = new StringBuilder();
        try {
            connection.setRequestMethod(method);
            connection.setDoOutput(true);
            BufferedWriter writer;
            if (data != null && !data.equals("")) {
                // 传参不为空才进行赋值
                writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), DEFAULT_ENCODE));
                writer.write(data);
                writer.flush();
                writer.close();
            }
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), DEFAULT_ENCODE));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                    builder.append(System.getProperty(LINE_SEPARATOR));
                }
                if (callback != null) {
                    callback.success(connection, builder.toString());
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
     * 将封装的map参数转换为字符串，以便写入输出流中
     */
    public static String getParamter(Map<String, String> map){
        StringBuilder builder = new StringBuilder();
        // encode下字符串，避免参数含有特殊字符
        try {
            for (Entry<String, String> entry : map.entrySet()) {
                builder.append(URLEncoder.encode(entry.getKey(), DEFAULT_ENCODE));
                builder.append("=");
                builder.append(URLEncoder.encode(entry.getValue(), DEFAULT_ENCODE));
                builder.append("&");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // 去掉最后一个&
        if (builder.length() > 0) {
            return builder.substring(0, builder.length() - 1);
        }
        return builder.toString();
    }
    
    
    /**
     * 格式化编码
     * @param str
     * @return
     */
    public static String encodeString(String str){
        String result = "";
        try {
            result = URLEncoder.encode(str, DEFAULT_ENCODE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * http请求接口回调接口
     */
    interface Callback{
        
        /**
         * 主要用于多线程回调
         * @param connection
         */
        void success(HttpURLConnection connection, String response);
    }
}