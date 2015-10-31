package Socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import Model.Note;
import Model.RecieverNote;

import com.thesis.ServerGUI;

public class Server {
	private static ServerSocket serverSocketA;
	private static Socket socket;
	private static PrintWriter printWriter;
	private int port;
	private ServerGUI sGui;
	private String ServerName = "Pi";
	private SocketControl socketControl;
	private boolean keepGoing;
	private ArrayList<ClientThread_> al;

	public Server(int port) {
		this(port, null);
	}

	public Server(int port, ServerGUI sGui) {
		this.port = port;
		this.sGui = sGui;
		socketControl = new SocketControl(this, sGui);
		al = new ArrayList<ClientThread_>();
	}

	public void StartSocket() {
		keepGoing = true;
		try {
			serverSocketA = new ServerSocket(port);
			while (keepGoing) {
				socket = serverSocketA.accept();
				if (!keepGoing)
					break;
				ClientThread_ t = new ClientThread_(socket);

				al.add(t);
				t.start();
			}

			try {
				serverSocketA.close();
				for (int i = 0; i < al.size(); ++i) {
					ClientThread_ tc = al.get(i);
					try {
						tc.recieve.close();
						tc.socket.close();
					} catch (IOException ioE) {
						ioE.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void StopSocket() {
		keepGoing = false;
		try {
			new Socket("localhost", port);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void SendMsg(String msg) {
		try {
			printWriter = new PrintWriter(socket.getOutputStream(), true);
			printWriter.println(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Sau nay can tao SocketControl cho moi thread.
	class ClientThread_ extends Thread {
		Socket socket;
		BufferedReader recieve;
		String tempMsg = "";
		Note note = new Note();
		RecieverNote recieveNote = new RecieverNote();

		ClientThread_(Socket socket) {
			this.socket = socket;
			// reciever
			try {
				recieve = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				String temp = "";
				while ((temp = recieve.readLine()) != null) {
					tempMsg += temp;
					System.out.println(tempMsg);
					if (temp != null && !temp.equals("")) {
						socketControl.getCommand(temp, note, recieveNote);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			// boolean keepGoing = true;
			// while (keepGoing) {
			// try {
			// String temp = "";
			// while ((temp = recieve.readLine()) != null) {
			// msgRecieve = msgRecieve + temp + "\n";
			// System.out.println(msgRecieve);
			// socketControl.getCommand();
			// }
			// } catch (IOException e) {
			// e.printStackTrace();
			// }
			// }
		}

		public void close() {
			try {
				if (socket != null)
					socket.close();
			} catch (Exception e) {
			}
		}
	}
}
