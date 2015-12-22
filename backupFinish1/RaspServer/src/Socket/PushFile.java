package Socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import com.thesis.UtilsMain;
import com.thesis.mmtt2011.homemms.model.ObjectFile;

public class PushFile extends Thread {
	private ServerSocket serverSocket = null;
	Socket socket = null;
	private int SOCKET_PORT = 6666;
	private String IPReceiver = null;
	private ArrayList<ObjectFile> objectFiles = new ArrayList<ObjectFile>();

	private boolean keepGoing = true;

	public PushFile(String IPReceiver, ArrayList<ObjectFile> objectFiles) throws IOException {
		this.objectFiles = objectFiles;
		serverSocket = new ServerSocket(SOCKET_PORT);
		serverSocket.setSoTimeout(10000);
		this.IPReceiver = IPReceiver;
	}

	@SuppressWarnings("finally")
	public void run() {

		while (true) {
			try {
				socket = serverSocket.accept();
				String ip = UtilsMain.getIPAddressFromSocket(socket);
				if (ip.equals(IPReceiver) && objectFiles.size()>0) {

					System.out.println("Server: Just connected to " + socket.getRemoteSocketAddress());
		             
		            DataInputStream in = new DataInputStream(socket.getInputStream());
//		            System.out.println("Server gets the message from client: "+in.readUTF());
		             
		            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
//		            out.writeUTF("Senver sent objects");

					ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
					objectOutputStream.writeObject(objectFiles);

					objectOutputStream.close();
					socket.close();
					System.out.println("Server Pushed to " + socket.getRemoteSocketAddress());
				}

			} catch (SocketTimeoutException s) {
				System.out.println("Server: Socket timed out!");
				break;
			} catch (IOException e) {
				e.printStackTrace();
				break;
			} finally {
				try {
					if (socket != null)
						socket.close();
					if (serverSocket != null)
						serverSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			}
		}
	}

	// public static void sendFileToClient(String path) {
	// FileInputStream fis = null;
	// BufferedInputStream bis = null;
	// OutputStream os = null;
	// ServerSocket serverSocket = null;
	// Socket socket = null;
	// try {
	// try {
	// serverSocket = new ServerSocket(SOCKET_PORT);
	// while (true) {
	// System.out.println("Waiting...");
	//
	// socket = serverSocket.accept();
	// System.out.println("Accepted connection : " + socket);
	//
	// // send file
	// File myFile = new File(path);
	// byte[] mybytearray = new byte[(int) myFile.length()];
	// fis = new FileInputStream(myFile);
	// bis = new BufferedInputStream(fis);
	// bis.read(mybytearray, 0, mybytearray.length);
	// os = socket.getOutputStream();
	// System.out.println("Sending " + path + "("
	// + mybytearray.length + " bytes)");
	// os.write(mybytearray, 0, mybytearray.length);
	// os.flush();
	// System.out.println("Done.");
	// // } finally {
	// if (bis != null)
	// bis.close();
	// if (os != null)
	// os.close();
	// if (socket != null)
	// socket.close();
	// break;
	// // }
	// }
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// } finally {
	// if (serverSocket != null)
	// try {
	// serverSocket.close();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// }

}
