package Model;

public class RaspberryPiClient  extends Device {
	 private boolean isConfigured;
//	    private Connection connection;
	    private boolean isChosenConfig;

	    public RaspberryPiClient(String deviceName, String IPAddress, String MacAddress) {
	        super(deviceName, IPAddress, MacAddress);
	        isConfigured = false;
	        isChosenConfig = true;
//	        connection = null;
	        this.username = "pi";
	        this.password = "raspberry";
	    }

	    public RaspberryPiClient(String deviceName, String IPAddress, String MacAddress, Boolean isConfigured, Boolean isChosenConfig) {
	        super(deviceName, IPAddress, MacAddress);
	        this.isConfigured = isConfigured;
	        this.isChosenConfig = isChosenConfig;
//	        connection = null;
	        this.username = "pi";
	        this.password = "raspberry";
	    }

	    @Override
	    public void setUsername(String username) {
	        super.setUsername(username);
	    }

	    @Override
	    public String getUsername() {
	        return super.getUsername();
	    }

	    @Override
	    public void setPassword(String password) {
	        super.setPassword(password);
	    }

	    @Override
	    public String getPassword() {
	        return super.getPassword();
	    }

	    public void setIsConfigured(boolean isConfigured) {
	        this.isConfigured = isConfigured;
	    }

	    public boolean getIsConfigured(){
	        return isConfigured;
	    }

//	    public void setConnection(Connection connection) {
//	        this.connection = connection;
//	    }
//
//	    public Connection getConnection() {
//	        return connection;
//	    }

	    public void setIsChosenConfig(boolean isChosenConfig) {
	        this.isChosenConfig = isChosenConfig;
	    }

	    public boolean getIsChosenConfig(){
	        return isChosenConfig;
	    }
}
