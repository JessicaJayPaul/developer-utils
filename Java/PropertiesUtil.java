import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {

	public static Properties loadProperties(String path) {
		Properties prop = new Properties();
		InputStream is = null;
		try {
			File file = new File(path);
			if (file.exists()) {
				// 读取本地资源文件
				is = new FileInputStream(file);
			} else {
				// 读取项目资源文件
				is = PropertiesUtil.class.getClassLoader().getResourceAsStream(path);
			}
			if (is != null) {
				prop.load(is);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return prop;
	}
}
