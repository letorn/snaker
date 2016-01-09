package util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

/*
 * 文件操作工具
 */
public class File {

	/**
	 * 读取文件内容
	 * @param filename 文件路径，包括文件名
	 * @return 文件内容
	 */
	public static String read(String filename) {
		try {
			StringBuffer buffer = new StringBuffer();
			InputStreamReader isr = new InputStreamReader(new FileInputStream(filename), "UTF-8");
			char[] chars = new char[1024];
			for (int len = isr.read(chars); len != -1; len = isr.read(chars))
				buffer.append(chars, 0, len);
			isr.close();
			return buffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 把内容写到文件中
	 * @param filename 文件路径，包括文件名
	 * @param content 文件内容
	 */
	public static void write(String filename, String content) {
		try {
			OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(filename), "UTF-8");
			osw.write(content);
			osw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 读取文件内容
	 * @param filename 文件路径，相对于当前项目，包括文件名
	 * @return 文件内容
	 */
	public static String readFromClasspath(String filename) {
		try {
			StringBuffer buffer = new StringBuffer();
			InputStreamReader isr = new InputStreamReader(new FileInputStream(classpathBy(filename)), "UTF-8");
			char[] chars = new char[1024];
			for (int len = isr.read(chars); len != -1; len = isr.read(chars))
				buffer.append(chars, 0, len);
			isr.close();
			return buffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 把内容写到文件中
	 * @param filename 文件路径，相对于当前项目，包括文件名
	 * @param content 文件内容
	 */
	public static void writeToClasspath(String filename, String content) {
		try {
			OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(classpathBy(filename)), "UTF-8");
			osw.write(content);
			osw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 读取文件输入流
	 * @param filename 文件路径，相对于当前项目，包括文件名
	 * @return 文件输入流
	 */
	public static InputStreamReader readStreamFromClasspath(String filename) {
		try {
			return new InputStreamReader(new FileInputStream(classpathBy(filename)), "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 根据相对路径获取绝对路径
	 * @param filename 文件的相对路径，相对于当前项目
	 * @return 文件的绝对路径
	 */
	public static String classpathBy(String filename) {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		URL url = classLoader.getResource(filename);
		if (url == null)
			url = File.class.getClassLoader().getResource(filename);
		if (url != null)
			return url.getFile();
		return null;
	}

}
