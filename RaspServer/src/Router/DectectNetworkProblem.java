package Router;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import com.thesis.ServerGUI;

public class DectectNetworkProblem extends Thread {

	private String nameInterface = null;
	private ServerGUI sGUI;

	public DectectNetworkProblem(ServerGUI sGUI) {
		this.sGUI = sGUI;
	}

	@Override
	public void run() {
		super.run();
		try {
			int limit = 0;
			while (true) {
				Thread.sleep(5000);
				limit += 1;

				// Get Name of interface.
				Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
				while (networkInterfaces.hasMoreElements()) {
					NetworkInterface networkInterface = (NetworkInterface) networkInterfaces.nextElement();
					if (networkInterface.getName().startsWith("wlan")) {
						nameInterface = networkInterface.getDisplayName();
					}
				}

				// Check RaspPI was got IP?
				String command = "ifconfig " + nameInterface;
				String respone = UtilsRouter.executeCommand(command);	
				if (respone.contains("inet addr")) {
					limit = 0;
					sGUI.UpdateMessageStateServerConnected();
					System.out.println("Server is connected.");
				} else {
					sGUI.UpdateMessageStateServer("Server isn't connected");
					System.out.println("Dont connected.");
					if (limit > 6) {
						sGUI.UpdateMessageStateServer("Start implementing Ad-hoc");
						System.out.println("Start implementing Ad-hoc");
						UtilsRouter.executeCommand(LoadCommand.loadShellInstallRouter());
					}
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
