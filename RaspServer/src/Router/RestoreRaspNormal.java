package Router;

import com.thesis.ServerGUI;

public class RestoreRaspNormal extends Thread{
	private ServerGUI sGui;
	
	public RestoreRaspNormal(ServerGUI sGui){
		this.sGui = sGui;
	}
	
	@Override
	public void run() {
		super.run();
		UtilsRouter.executeCommand(LoadCommand.loadShellInstallNormal());
	}

}
