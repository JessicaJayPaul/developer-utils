import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;

public class FileUtil {

	/**
	 * 系统换行符
	 */
	public static final String LINE_SEPARATOR = "line.separator";
	
	/**
	 * 读取文本文件
	 */
	public static String getStrFromFile(String filePath) {
		StringBuilder builder = new StringBuilder();
		File file = new File(filePath);
		if (!file.exists()) {
			return "";
		}
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(file)));
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
				builder.append(System.getProperty(LINE_SEPARATOR));
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return builder.toString();
	}

	/**
	 * 使用文件通道的方式高效复制文件
	 */
	public static boolean fileChannelCopy(File file, File tarFile) {
		if (!file.exists()) {
			return false;
		}
		// 若目标文件存在，则删除替换
		if (tarFile.exists()) {
			tarFile.delete();
		}
		FileInputStream fis = null;
		FileOutputStream fos = null;
		FileChannel in = null;
		FileChannel out = null;
		try {
			fis = new FileInputStream(file);
			fos = new FileOutputStream(tarFile);
			// 得到对应的文件通道
			in = fis.getChannel();
			out = fos.getChannel();
			// 连接两个通道，并且从in通道读取，然后写入out通道
			in.transferTo(0, in.size(), out);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
				if (in != null) {
					in.close();
				}
				if (fos != null) {
					fos.close();
				}
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
}
