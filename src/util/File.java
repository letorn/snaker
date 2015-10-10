package util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

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
			InputStreamReader onputStreamReader = new InputStreamReader(new FileInputStream(filename), "UTF-8");
			char[] chars = new char[1024];
			for (int len = onputStreamReader.read(chars); len != -1; len = onputStreamReader.read(chars))
				buffer.append(chars, 0, len);
			onputStreamReader.close();
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
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(filename), "UTF-8");
			outputStreamWriter.write(content);
			outputStreamWriter.close();
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
			InputStreamReader onputStreamReader = new InputStreamReader(new FileInputStream(classpathBy(filename)), "UTF-8");
			char[] chars = new char[1024];
			for (int len = onputStreamReader.read(chars); len != -1; len = onputStreamReader.read(chars))
				buffer.append(chars, 0, len);
			onputStreamReader.close();
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
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(classpathBy(filename)), "UTF-8");
			outputStreamWriter.write(content);
			outputStreamWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据相对路径获取绝对路径
	 * @param filename 文件的相对路径，相对于当前项目
	 * @return 文件的绝对路径
	 */
	private static String classpathBy(String filename) {
		return System.class.getResource(filename).getFile();
	}

}
