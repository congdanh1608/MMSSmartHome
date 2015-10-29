package com.thesis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.util.ArrayList;

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
		try {
			File file = new File(path);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(file, false);
			Writer writer = new BufferedWriter(new OutputStreamWriter(fos, "utf-8"));
			writer.write(content);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
