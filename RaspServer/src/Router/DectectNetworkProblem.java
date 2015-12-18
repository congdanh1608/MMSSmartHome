package Router;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class DectectNetworkProblem extends Thread {

	private String nameInterface = null;

	public DectectNetworkProblem() {

	}

	@Override
	public void run() {
		super.run();
		try {
			int limit = 0;
			while (true) {
				Thread.sleep(2000);
				limit += 1000;

				// Get Name of interface.
				Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
				while (networkInterfaces.hasMoreElements()) {
					NetworkInterface networkInterface = (NetworkInterface) networkInterfaces.nextElement();
//					if (networkInterface.getName().startsWith("eth")) {
					if (networkInterface.getName().startsWith("wlan")) {
						nameInterface = networkInterface.getDisplayName();
					}
				}

				// Check RaspPI was got IP?
				String command = "ifconfig " + nameInterface;
				String respone = UtilsRouter.executeCommand(command);	
				if (respone.contains("inet addr")) {
					limit = 0;
					System.out.println("Has connected.");
				} else {
					System.out.println("Dont connected.");
					if (limit > 5000) {
						System.out.println("Start implement AP");
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
