package com.thesis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;

import Model.Device;

public class NetworkDiscover {
	private static ArrayList<Device> listDevice;
	private static String startIP;
	private static String endIP;
	private static ArrayList<IpAddressWithSubnet> listOfIpAddressWithSubnet;

	public NetworkDiscover() {
		listDevice = new ArrayList<Device>();
		startIP = null;
		endIP = null;
		listOfIpAddressWithSubnet = new ArrayList<IpAddressWithSubnet>();;
	}

	public ArrayList<Device> startDiscover() {
		// Find List IPAddress.
		listOfIpAddressWithSubnet = GetInterfaceAddresses();
		if (listOfIpAddressWithSubnet != null) {
			FindRangeOfIPSubner();
			if (startIP != null && endIP != null) {
				// String HexStartAddress = fromIpToHex("192.168.1.100");
				// String HexEndAddress = fromIpToHex("192.168.1.150");
				String HexStartAddress = fromIpToHex(startIP);
				String HexEndAddress = fromIpToHex(endIP);
				// Mutil-Thread. 4 Thread
				ArrayList<String> ipSlip = new ArrayList<String>();
				int d = 0;
				ipSlip = slipIP(HexStartAddress, HexEndAddress);
				ArrayList<Thread> threads = new ArrayList<Thread>();
				for (int i = 0; i < 4; i++) {
					Thread thread = MyThread(ipSlip.get(d), ipSlip.get(d + 1));
					threads.add(thread);
					thread.start();
					d += 2;
				}
				try {
					System.out.println("Waiting for threads to finish.");
					for (int i = 0; i < 4; i++) {
						threads.get(i).join();
					}
				} catch (InterruptedException e) {
					System.out.println("Main thread Interrupted");
				}
			}
		}
		return listDevice;
	}

	public static ArrayList<NetworkInterface> getNetworkInterface() {
		ArrayList<NetworkInterface> result = new ArrayList<NetworkInterface>();
		try {
			Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
					.getNetworkInterfaces();
			while (enumNetworkInterfaces.hasMoreElements()) {
				NetworkInterface networkInterface = enumNetworkInterfaces
						.nextElement();
				if (networkInterface.isUp() && !networkInterface.isLoopback()) {
					result.add(networkInterface);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static ArrayList<IpAddressWithSubnet> GetInterfaceAddresses() {
		ArrayList<NetworkInterface> listInter = getNetworkInterface();
		ArrayList<IpAddressWithSubnet> listIP = new ArrayList<IpAddressWithSubnet>();
		for (NetworkInterface networkInterface : listInter) {
			try {
				List<InterfaceAddress> listOfInterfaceAddresses = networkInterface
						.getInterfaceAddresses();
				for (InterfaceAddress interfaceAddress : listOfInterfaceAddresses) {
					if (interfaceAddress.getAddress() instanceof Inet4Address) {
						int subnet = interfaceAddress.getNetworkPrefixLength();
						System.out.println("address "
								+ interfaceAddress.getAddress()
										.getHostAddress()
								+ "/"
								+ subnet
								+ ", is siteLocal "
								+ interfaceAddress.getAddress()
										.isSiteLocalAddress());
						IpAddressWithSubnet ip = new IpAddressWithSubnet();
						ip.setIpAddress(interfaceAddress.getAddress()
								.getHostAddress());
						ip.setSubnet(subnet);
						if (subnet != 32)
							listIP.add(ip);
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return listIP;
	}

	public static void FindRangeOfIPSubner() {
		for (IpAddressWithSubnet ipAddressWithSubnet : listOfIpAddressWithSubnet) {
			String IPAddress = ipAddressWithSubnet.getIpAddress();
			int SubnetMask = ipAddressWithSubnet.getSubnet();
			long longIp = ipToLong(IPAddress);
			String binaryLongIp = Long.toBinaryString(longIp);
			if (binaryLongIp.length() < 32) {
				int add = 32 - binaryLongIp.length();
				for (int i = 0; i < add; i++)
					binaryLongIp = "0" + binaryLongIp;
			}
			String startAddress = "";
			String endAddress = "";
			for (int i = 0; i < SubnetMask; i++) {
				startAddress += binaryLongIp.charAt(i);
				endAddress += binaryLongIp.charAt(i);
			}
			for (int i = 0; i < 32 - SubnetMask; i++) {
				startAddress += "0";
				endAddress += "1";
			}

			startIP = fromBinaryToIp(startAddress);
			endIP = fromBinaryToIp(endAddress);
		}
	}

	private static long ipToLong(String ipAddress) {
		long result = 0;
		String[] atoms = ipAddress.split("\\.");
		for (int i = 3; i >= 0; i--) {
			result |= (Long.parseLong(atoms[3 - i]) << (i * 8));
		}
		return result & 0xFFFFFFFF;
	}

	private static String longToIp(long ip) {
		StringBuilder sb = new StringBuilder(15);
		for (int i = 0; i < 4; i++) {
			sb.insert(0, Long.toString(ip & 0xff));
			if (i < 3) {
				sb.insert(0, '.');
			}
			ip >>= 8;
		}
		return sb.toString();
	}

	public static String fromBinaryToIp(String binary) {
		return longToIp(Long.parseLong(binary, 2));
	}

	public static String fromIpToHex(String ipAddress) {
		return Long.toHexString(ipToLong(ipAddress));

	}

	public static String fromHexToIp(String hex) {
		return longToIp(Long.parseLong(hex, 16));
	}

	private static String addTwoHexToString(String a, String b) {
		long number = Long.parseLong(a, 16) + Long.parseLong(b, 16);
		return Long.toHexString(number);
	}

	private static int countIP(String hexStartAddress, String hexEndAddress) {
		int count = 0;
		while (!hexStartAddress.equals(hexEndAddress)) {
			count++;
			hexStartAddress = addTwoHexToString(hexStartAddress, "01");
		}
		return count;
	}

	private static ArrayList<String> slipIP(String hexStartAddress,
			String hexEndAddress) {
		int total = 0, spart = 0;
		ArrayList<String> ipSlip = new ArrayList<String>();
		total = countIP(hexStartAddress, hexEndAddress);
		// chia 4
		spart = (int) total / 4;
		// 0_1--2_3--4_5--6_7
		// 0 - 1
		ipSlip.add(0, hexStartAddress);
		for (int i = 0; i < spart; i++) {
			hexStartAddress = addTwoHexToString(hexStartAddress, "01");
		}
		ipSlip.add(1, hexStartAddress);
		// 2 - 3
		hexStartAddress = addTwoHexToString(hexStartAddress, "01");
		ipSlip.add(2, hexStartAddress);
		for (int i = 0; i < spart; i++) {
			hexStartAddress = addTwoHexToString(hexStartAddress, "01");
		}
		ipSlip.add(3, hexStartAddress);
		// 4 - 5
		hexStartAddress = addTwoHexToString(hexStartAddress, "01");
		ipSlip.add(4, hexStartAddress);
		for (int i = 0; i < spart; i++) {
			hexStartAddress = addTwoHexToString(hexStartAddress, "01");
		}
		ipSlip.add(5, hexStartAddress);
		// 6 - 7
		ipSlip.add(6, addTwoHexToString(hexStartAddress, "01"));
		ipSlip.add(7, hexEndAddress);
		return ipSlip;
	}

	public static String getMacFromArpCache(String ip) {
		if (ip == null)
			return null;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader("/proc/net/arp"));
			String line;
			while ((line = br.readLine()) != null) {
				String[] splitted = line.split(" +");
				if (splitted != null && splitted.length >= 4
						&& ip.equals(splitted[0])) {
					// Basic sanity check
					String mac = splitted[3];
					if (mac.matches("..:..:..:..:..:..")) {
						return mac;
					} else {
						return "Unknown";
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return "Unknown";
	}

	private static boolean CheckMacPi(String MacAddress) {
		if (MacAddress.contains("b8:27:eb")) {
			return true;
		}
		return false;
	}

	private static Thread MyThread(final String start, final String end) {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				String start1 = start;
				while (!start1.equals(end)) {
					final String currentIpAddress = fromHexToIp(start1);
					InetAddress inetAddress = null;
					try {
						inetAddress = InetAddress.getByName(currentIpAddress);
						if (inetAddress.isReachable(500)) {
							// String HostName =
							// getHostNameNmblookup(currentIpAddress,
							// nmbLookupLocation);
							String HostName = "Unknown";
							String MacAddress = getMacFromArpCache(currentIpAddress);
							if (HostName.contains("Unknown")
									&& !MacAddress.contains("Unknown")
									&& CheckMacPi(MacAddress)) {
								HostName = "Raspberry Pi";
							}
							Device host;
							if (HostName.contains("Unknown")) {
								host = new Device(HostName, currentIpAddress,
										MacAddress);
							} else if (HostName.contains("Raspberry Pi")) {
								host = new Device(HostName, currentIpAddress,
										MacAddress, 1);
							} else {
								host = new Device(HostName, currentIpAddress,
										MacAddress, 2);
							}
							listDevice.add(host);
						}
					} catch (UnknownHostException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					start1 = addTwoHexToString(start1, "01");
				}
			}
		});
		return thread;
	}
}

class IpAddressWithSubnet {
	private String ipAddress;
	private int subnet;

	public int getSubnet() {
		return subnet;
	}

	public void setSubnet(int subnet) {
		this.subnet = subnet;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

}
