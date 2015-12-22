package Router;

public class RestoreRaspNormal extends Thread{
	
	public RestoreRaspNormal(){
		
	}
	
	@Override
	public void run() {
		super.run();
		UtilsRouter.executeCommand(LoadCommand.loadShellInstallNormal());
	}

}
