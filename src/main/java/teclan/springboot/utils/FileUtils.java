package teclan.springboot.utils;

import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class FileUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);

	public static String getContent(File file) {
		StringBuilder content = new StringBuilder();
		try {

			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(new FileInputStream(file));
				BufferedReader bufferedReader = new BufferedReader(read);
				String line = null;
				while ((line = bufferedReader.readLine()) != null) {
					content.append(line);
				}
				read.close();
			} else {
				LOGGER.error(String.format("找不到指定的文件:%s", file.getAbsolutePath()));
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
		return content.toString();
	}

	/**
	 * 生成文件
	 * 
	 * @throws Exception
	 */
	public static void createFile(String path) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("");
		write2File(path, sb);
	}

	/**
	 * 删除文件
	 * 
	 */
	public static void deleteFile(String path) {
		try {
			File file = new File(path);

			if(!file.exists()){
				LOGGER.warn("文件不存在，调过删除！{}",path);
				return;
			}

			File[] files = file.listFiles();
			for(File f:files){
				if(f.isFile()){
					LOGGER.info("正在删除:{}",file.getAbsolutePath());
					f.delete();
				}else{
					deleteFile(f.getAbsolutePath());
					LOGGER.info("正在删除:{}",file.getAbsolutePath());
					f.delete();
				}
			}
			LOGGER.info("正在删除:{}",file.getAbsolutePath());
			file.delete();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	public static  boolean exists(File file){
		return file.exists();
	}

	public static  boolean exists(String file){
		return new File(file).exists();
	}

	public static synchronized void write2File(String filePath, StringBuffer content) throws Exception {
		write2File(filePath, "UTF-8", content);
	}

	public static void write2File(String filePath, String coding, StringBuffer content) throws Exception {
		FileOutputStream out = null;
		OutputStreamWriter osw = null;
		try {
			if (!new File(filePath).exists()) {
				if (!new File(filePath).getParentFile().exists()) {
					if (new File(filePath).getParentFile().mkdirs()) {
						new File(filePath).createNewFile();
					}
				} else {
					new File(filePath).createNewFile();
				}
			}
			FileOutputStream fos = null;
			OutputStreamWriter writer = null;
			fos = new FileOutputStream(new File(filePath),true);
			writer = new OutputStreamWriter(fos, coding);
			writer.write(content.toString());
			writer.flush();
			writer.close();
		} catch (Exception e) {
			throw new Exception(e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
			if (osw != null) {
				try {
					osw.close();
				} catch (IOException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		}
	}

	public  static void rename(File src,File des){
		LOGGER.info("重命名文件：{}->{}",src.getAbsolutePath(),des.getAbsolutePath());
		src.renameTo(des);
	}
	public static void copy(File src,File des) throws IOException {

		if(!exists(src)){
			LOGGER.warn("源文件不存在,{}",src);
			return;
		}

		File[] files = src.listFiles();
		for(File file:files){
			File target = new File(file.getAbsolutePath().replace(src.getAbsolutePath(),des.getAbsolutePath()));
			if(file.isDirectory()){
				copy(file,target);
			}else{
				try{
					new File(target.getParent()).mkdirs();
				}catch (Exception e){
					LOGGER.error(e.getMessage(), e);
				}
				LOGGER.info("正在复制文件,{}->{}",file.getAbsolutePath(),target.getAbsolutePath());
				Files.copy(file,target);
				LOGGER.info("复制完成,{}->{}",file.getAbsolutePath(),target.getAbsolutePath());
			}
		}

	}
}
