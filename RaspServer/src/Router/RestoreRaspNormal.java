package Router;

import com.thesis.ServerGUI;

public class RestoreRaspNormal extends Thread{
	private ServerGUI sGUI;
	
	public RestoreRaspNormal(ServerGUI sGUI){
		this.sGUI = sGUI;
	}
	
	@Override
	public void run() {
		super.run();
		if (sGUI!=null){
			sGUI.UpdateMessageStateServer("Start implementing AP");
		}
		UtilsRouter.executeCommand(LoadCommand.loadShellInstallNormal());
	}

}
