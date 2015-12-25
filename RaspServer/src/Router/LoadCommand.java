package Router;

public class LoadCommand {
	
	public static String loadShellInstallRouter(){
		return "sudo sh /home/pi/shell/implement.sh ad-hoc";
	}
	
	public static String loadShellInstallNormal(){
		return "sudo sh /home/pi/shell/implement.sh normal";
	}
	
	public static String loadCommandInstallRaspRouter(){
		return "sudo dpkg -i /home/pi/deb/dep_router/hostapd_2.3-1+deb8u3_armhf.deb "
				+ "&& sudo dpkg -i /home/pi/deb/dep_router/isc-dhcp-server_4.3.1-6_armhf.deb "
				+ "&& sudo dpkg -i /home/pi/deb/dep_router/libnl-route-3-200_3.2.24-2_armhf.deb "
				+ "&& sudo cp /home/pi/wifi_router/interfaces /etc/network/ "
				+ "&& sudo cp /home/pi/wifi_router/hostapd.conf /etc/hostapd/ "
				+ "&& sudo cp /home/pi/wifi_router/hostapd /etc/default/ "
				+ "&& sudo cp /home/pi/wifi_router/dhcpd.conf /etc/dhcp/ "
				+ "&& sudo reboot";
	}
	
	public static String loadCommandRestoreRasp(){
		return "sudo cp /home/pi/bak/interfaces /etc/network/ "
				+ "&& sudo rm -rf /etc/hostapd/hostapd.conf "
				+ "&& rm -rf /etc/default/hostapd "
				+ "&& sudo rm -rf /etc/dhcp/dhcpd.conf "
				+ "&& sudo rm /usr/sbin/hostapd & sudo apt-get -y purge isc-dhcp-server";
	}
	
	public static String loadCommandReboot(){
		return "sudo reboot";
	}
}
