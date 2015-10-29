package com.thesis;

import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import Model.Device;

public class MainActivity extends Frame implements ActionListener {
	public static ArrayList<Device> listDevice = new ArrayList<Device>();
	private boolean isFinishScan = true;

	// Awt
	Label label = new Label("Scan Lan Network!");
	Button btnScan = new Button("Scan");
	Button btnClear = new Button("Clean");
	Button btnExit = new Button("Exit");
	Label label2 = new Label("Wait Action");
	static JProgressBar bar = new JProgressBar(0, 100);
	TextArea tArea = new TextArea();

	public MainActivity(String title) {
		super(title);
		setLayout(new FlowLayout());
		add(label);
		add(btnScan);
		add(btnClear);
		add(btnExit);
		btnScan.addActionListener(this);
		btnClear.addActionListener(this);
		btnExit.addActionListener(this);
		add(label2);
		add(tArea);
	}

	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource() == btnScan) {
			final JFrame frame = new JFrame();
		    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		    frame.setSize(400, 50);
		    frame.setVisible(true);
		    final DefaultBoundedRangeModel model = new DefaultBoundedRangeModel();
		    frame.add(new JProgressBar(model));

			label2.setText("Scanning...");
			ScanInBackground();
			
			// Update process (demo)
			bar.setValue(0);
			Thread t = new Thread(new Runnable() {
		        public void run() {
		            int i = 1;
		            model.setMinimum(0);
		            model.setMaximum(1000);
		            try {
		                while (!isFinishScan && (i <= 100 || true)) {
		                    model.setValue(i);
		                    i++;
		                    Thread.sleep(100);
		                }
		                frame.setVisible(false);
		            } catch (InterruptedException ex) {
		                model.setValue(model.getMaximum());
		            }
		        }
		    });
		    t.start();
			
			label2.setText("Finish Scan");
			bar.setValue(100);
		}
		if (ae.getSource() == btnClear) {
			tArea.setText("");
		}
		if (ae.getSource() == btnExit) {
			System.exit(0);
		}
	}

	public static void main(String[] args) throws SocketException {
		// Awt
		MainActivity mainActivity = new MainActivity("Scan Application");
		mainActivity.setSize(600, 400);
		mainActivity.show();

		/*
		 * Scanner consoleIn = new Scanner(System.in); System.out .print(
		 * "1)Print your IPv4 address\n2)Print your IPv6 adress\n3)Print reachable IPv4 hosts: "
		 * ); int choice = consoleIn.nextInt(); consoleIn.close(); if (choice ==
		 * 1 || choice == 2) { String protocolVersion = choice == 1 ? "IPv4" :
		 * "IPv6"; InetAddress address = getWLANipAddress(protocolVersion);
		 * System.out.println(address != null ? address : protocolVersion +
		 * " address not found. Is your internet down?"); } else if (choice ==
		 * 3) { NetworkDiscover netD = new NetworkDiscover();
		 * System.out.print("start"); listDevice = netD.startDiscover(); for
		 * (int i = 0; i<listDevice.size(); i++){
		 * System.out.print(listDevice.get(i).getDeviceName());
		 * System.out.print("\n" + listDevice.get(i).getIPAddress());
		 * System.out.println("\n" + listDevice.get(i).getMacAddress()); } }
		 * else { System.out.println("Unknown choice."); }
		 */
	}

	private void ScanInBackground() {
		SwingWorker worker = new SwingWorker<ArrayList<Device>, Void>() {
			@Override
			public ArrayList<Device> doInBackground() {
				isFinishScan = false;

				// Start Scan
				NetworkDiscover netD = new NetworkDiscover();
				return netD.startDiscover();
			}

			@Override
			protected void process(List<Void> chunks) {
				super.process(chunks);
			}

			@Override
			public void done() {
				isFinishScan = true;
				try {
					listDevice = get();
					for (int i = 0; i < listDevice.size(); i++) {
						tArea.append(listDevice.get(i).getDeviceName() + "\n");
						tArea.append(listDevice.get(i).getIPAddress() + "\n");
						tArea.append(listDevice.get(i).getMacAddress() + "\n\n");
					}
				} catch (InterruptedException ignore) {
				} catch (java.util.concurrent.ExecutionException e) {
					String why = null;
					Throwable cause = e.getCause();
					if (cause != null) {
						why = cause.getMessage();
					} else {
						why = e.getMessage();
					}
					System.err.println("Error retrieving file: " + why);
				}
			}
		};
		worker.execute();
	}

	public static InetAddress getWLANipAddress(String protocolVersion)
			throws SocketException {
		Enumeration<NetworkInterface> nets = NetworkInterface
				.getNetworkInterfaces();
		for (NetworkInterface netint : Collections.list(nets)) {
			if (netint.isUp() && !netint.isLoopback() && !netint.isVirtual()) {
				Enumeration<InetAddress> inetAddresses = netint
						.getInetAddresses();
				for (InetAddress inetAddress : Collections.list(inetAddresses)) {
					if (protocolVersion.equals("IPv4")) {
						if (inetAddress instanceof Inet4Address) {
							return inetAddress;
						}
					} else {
						if (inetAddress instanceof Inet6Address) {
							return inetAddress;
						}
					}
				}
			}
		}
		return null;
	}
}
