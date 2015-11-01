package SSH;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class PushFile {
	static ServerSocket server = null;
	static int SOCKET_PORT = 6666;

	public static void sendFileToClient(String path) {
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		OutputStream os = null;
		ServerSocket serverSocket = null;
		Socket socket = null;
		try {
			try {
				serverSocket = new ServerSocket(SOCKET_PORT);
				while (true) {
					System.out.println("Waiting...");

					socket = serverSocket.accept();
					System.out.println("Accepted connection : " + socket);

					// send file
					File myFile = new File(path);
					byte[] mybytearray = new byte[(int) myFile.length()];
					fis = new FileInputStream(myFile);
					bis = new BufferedInputStream(fis);
					bis.read(mybytearray, 0, mybytearray.length);
					os = socket.getOutputStream();
					System.out.println("Sending " + path + "("
							+ mybytearray.length + " bytes)");
					os.write(mybytearray, 0, mybytearray.length);
					os.flush();
					System.out.println("Done.");
					// } finally {
					if (bis != null)
						bis.close();
					if (os != null)
						os.close();
					if (socket != null)
						socket.close();
					break;
					// }
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} finally {
			if (serverSocket != null)
				try {
					serverSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
}
