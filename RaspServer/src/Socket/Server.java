package Socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import com.thesis.ServerGUI;

import presistence.ContantsHomeMMS;

public class Server {
	private static ServerSocket serverSocketA;
	private static Socket socket;
	private int port;
	private ServerGUI sGui;
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
//				socket.setKeepAlive(true);
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

	// Sau nay can tao SocketControl cho moi thread.
	class ClientThread_ extends Thread {
		Socket socket;
		BufferedReader receive;
		String tempMsg = "";
		SocketControl socketControl;

		ClientThread_(Socket socket) {
			this.socket = socket;
			socketControl = new SocketControl(this, socket, sGui);
		}

		@Override
		public void run() {
			// reciever
			try {
				receive = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String temp = "";
				while ((temp = receive.readLine()) != null) {
					tempMsg += temp;
					System.out.println("Server receive: " + temp);
					if (temp != null && !temp.equals("")) {
						socketControl.getCommand(temp);

						// Check keyword ENDNOTE
						// Call function Check user online
						// --> Call function in all Thread client to check New
						// Update User Database.
						if (socketControl.checkNewRegister(temp)) {
							Thread.sleep(1000);
							UpdateUserDatabaseToAllThreadClient();
						}
						
						// Message
						if (socketControl.checkReceiveEndNote(temp)) {
							Thread.sleep(1000);
							CheckNewMessageForThreadClient();
						}

						// Check all client online or offline.
						if (socketControl.checkNewClientConnect(temp)) {
							CheckConnectAllClient(this);
						}
					}
				}
				// This case for user kill app.
				if (temp == null) {
					socketControl.setStatusForUser(ContantsHomeMMS.UserStatus.offline.name());
					removeClientThread(this);
					System.out.println("Client " + socketControl.user.getId() + "disconnect");
				}

			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void close() {
			System.out.println("Close connect " + socketControl.user.getId());
			try {
				if (socket != null && !socket.isClosed())
					socket.close();
				if (receive != null) {
					receive.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private boolean writeMsg(Socket sk) {
			try {
				if (!sk.isConnected()) {
					sk.close();
					return false;
				}
				BufferedReader input = new BufferedReader(new InputStreamReader(sk.getInputStream()));
				if (input.read() == -1) {
					return false;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		}

		// Check connection of all client -> set status online/offline for user.
		// This case for user lose connection.
		private void CheckConnectAllClient(ClientThread_ ct) {
			if (clients != null) {
				// Set all client offline.
				// socketControl.setStatusForAllUser(ContantsHomeMMS.UserStatus.offline.name());

				// Check status of client.
				for (ClientThread_ client : clients) {
					if (writeMsg(client.socket)) {
						if (client.socketControl.user.getId() != null) {
							client.socketControl.setStatusForUser(ContantsHomeMMS.UserStatus.online.name());
							System.out.println(client.socketControl.user.getId() + " still connect.");
						}
					} else {
						client.socketControl.setStatusForUser(ContantsHomeMMS.UserStatus.offline.name());
						removeClientThread(ct);
						System.out.println(client.socketControl.user.getId() + " disconnect.");
					}
				}
			}
		}
		
		public void UpdateUserDatabaseToAllThreadClient() {
			if (clients != null) {
				for (ClientThread_ client : clients) {
					client.socketControl.sendAskWasRegitered(socketControl.user.getId());
				}
			}
		}

		public void CheckNewMessageForThreadClient() {
			if (clients != null) {
				for (ClientThread_ client : clients) {
					if (socketControl.checkUserIsReceive(client.socketControl.user.getId())) {
						client.socketControl.checkNewMessageSendToClient();
					}
				}
			}
		}

	}

	private void removeClientThread(ClientThread_ ct) {
		for (int i = 0; i < clients.size(); i++) {
			if (ct.socket.getPort() == clients.get(i).socket.getPort()) {
				clients.remove(i);
			}
		}
	}
}
