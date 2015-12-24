package com.thesis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.NetworkInterface;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

public class UtilsMain {
	static byte[] appClientData;

	public static void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}

	public static void createFile(String path, String content) {
		File file = new File(path);
		FileOutputStream fos = null;
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			fos = new FileOutputStream(file, false);
			Writer writer = new BufferedWriter(new OutputStreamWriter(fos, "utf-8"));
			writer.write(content);
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Boolean createFolder(String dir) {
		File folder = new File(dir);
		if (folder.exists()) {
			return true;
		} else {
			try {
				folder.mkdirs();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
	}

	public static boolean checkFilsIsExits(String filePath) {
		File file = new File(filePath);
		if (file.exists())
			return true;
		else
			return false;
	}

	public static String readFile(String path) {
		String s = "";
		File file = new File(path);
		if (!file.exists()) {
			return null;
		}
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			int content;
			while ((content = fis.read()) != -1) {
				s += (char) content;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return s;
	}

	public static String createMessageID(String mac) {
		String mID = null;
		if (mac != null && !mac.equals("")) {
			mID = mac.substring(9, 10) + mac.substring(12, 13) + mac.substring(15, 16) + getCurrentTimeHms();
		}
		return mID;
	}

	public static String getCurrentTimeHms() {
		String time = null;
		DateFormat dateFormat = new SimpleDateFormat("yyyymmddHHmmss");
		dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
		Date date = new Date();
		time = dateFormat.format(date); // 20151030075959
		return time;
	}

	public static String getCurrentTime() {
		String time = null;
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
		dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
		Date date = new Date();
		time = dateFormat.format(date); // 20151030_075959
		return time;
	}

	public static String getIPAddressFromSocket(Socket socket) {
		String ip = null;
		String ip_temp = socket.getRemoteSocketAddress().toString();
		ip = ip_temp.substring(1, ip_temp.lastIndexOf(":"));
		return ip;
	}

	public static String randomPassword() {
		Random rand = new Random();
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < 5; i++) {
			char c = (char) (rand.nextInt(26) + 'a');
			builder.append(c);
		}
		return builder.toString();
	}

}
