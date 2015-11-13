package Socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import com.thesis.ServerGUI;

import Model.Message;

public class Server {
	private static ServerSocket serverSocketA;
	private static Socket socket;
	private static PrintWriter printWriter;
	private int port;
	private ServerGUI sGui;
	private String ServerName = "Pi";
	private boolean keepGoing;
	private ArrayList<ClientThread_> clients;

	public Server(int port) {
		this(port, null);
	}

	public Server(int port, ServerGUI sGui) {
		this.port = port;
		this.sGui = sGui;
		// socketControl = new SocketControl(this, sGui);
		clients = new ArrayList<ClientThread_>();
	}

	public void StartSocket() {
		keepGoing = true;
		try {
			serverSocketA = new ServerSocket(port);
			while (keepGoing) {
				socket = serverSocketA.accept();
				System.out.println("Accepted connection : " + socket);
				if (!keepGoing)
					break;
				ClientThread_ clientThread = new ClientThread_(socket);

				clients.add(clientThread);
				clientThread.start();
			}

			try {
				serverSocketA.close();
				for (int i = 0; i < clients.size(); ++i) {
					ClientThread_ tc = clients.get(i);
					try {
						tc.receive.close();
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
			System.out.println("Server send " + msg);
			printWriter = new PrintWriter(socket.getOutputStream(), true);
			printWriter.println(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Sau nay can tao SocketControl cho moi thread.
	class ClientThread_ extends Thread {
		Socket socket;
		BufferedReader receive;
		String tempMsg = "";
		SocketControl socketControl = new SocketControl(Server.this);

		ClientThread_(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			// reciever
			try {
				receive = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String temp = "";
				while ((temp = receive.readLine()) != null) {
					tempMsg += temp;
					System.out.println(tempMsg);
					if (temp != null && !temp.equals("")) {
						socketControl.getCommand(temp);
					}
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void close() {
			try {
				if (socket != null)
					socket.close();
				if (receive !=null){
					receive.close();
				}
			} catch (Exception e) {
			}
		}
	}
}
