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
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Utils {
	static byte[] appClientData;

	public static void copyFile(InputStream in, OutputStream out)
			throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}

	public static ArrayList<String> addCommandsReboot() {
		ArrayList<String> listOfCommands = new ArrayList<String>();
		listOfCommands.add("sudo reboot\n");
		return listOfCommands;
	}

	public static int CheckIsRaspConfigured(String IPAddress, String username,
			String password) {
		// Connection connection = new Connection(IPAddress);
		// try {
		// connection.connect(null, 3000, 3000);
		// if (connection.authenticateWithPassword(username, password)) {
		// String s = "ls /home/" + username;
		// String result = excCommandStringWithResult(connection, s);
		// if (result != null && result.contains("configrasppi")) {
		// return 1; // It is Rasp was configure.
		// }
		// return 2; // It is Rasp was not configure.
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		return 0; // is not Rasp
	}

	public static void createFile(String path, String content) {
		File file = new File(path);
		FileOutputStream fos = null;
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			fos = new FileOutputStream(file, false);
			Writer writer = new BufferedWriter(new OutputStreamWriter(fos,
					"utf-8"));
			writer.write(content);
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
				s +=(char) content;
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

	public static String getCurrentTime() {
		String time = null;
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
		Date date = new Date();
		time = dateFormat.format(date); // 20151030_075959
		return time;
	}
}
