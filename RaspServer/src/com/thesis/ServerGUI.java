package com.thesis;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.TimeZone;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import Database.DatabaseHandler;
import Database.MessageModel;
import Database.UserModel;
import Database.Utils;
import Model.Message;
import Model.User;
import Network.DiscoveryThread;
import Router.DectectNetworkProblem;
import Router.LoadCommand;
import Router.UtilsRouter;
import Socket.Server;
import presistence.ContantsHomeMMS;

public class ServerGUI {

	private JFrame frame;
	private JLabel lblServerStatus, lblTime, lblCurrentday, lblCurrenttime;;

	private JButton btnStartListening, btn2;
	private JPanel panelLeft, panelRight;
	private int col = 4, rows = 4;
	private double ratio;
	private List<JPanel> jPanels;
	private JLabel lblSender, lblTitle;
	private JTextArea tAMessage;

	private static Server server = null;
	private static int port = 2222;
	private UserModel userModel;
	private MessageModel messageModel;
	private DatabaseHandler databaseHandler;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ServerGUI window = new ServerGUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ServerGUI() {
		initialize();
		// Set Icon
		setIcon();
		// Update time in GUI of Server.
		UpdateTime();
		// Check database and create new if not exits.
		FirstCheckDatabase();
		// Check folder of app and create new if not exits.
		createFolderApp();
		// Show message into GUI of Server.
		ShowMessage(false);
		// Check reset all status client to offline.
		ResetStatusForAllUser();

		// Run Server listen.
		startSocketListener();
		// start socket listen broadcast
		startListenBroadcase();
		// start dectect network problem.
//		startDectectNetworkProblem();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		jPanels = new ArrayList<JPanel>();
		databaseHandler = new DatabaseHandler();
		userModel = new UserModel();
		messageModel = new MessageModel();

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		ratio = screenSize.getWidth() / 1366;
		int widthLeft = (int) (screenSize.width * 0.82);
		int xright = (int) (screenSize.getWidth() * 0.82);
		int widthRight = screenSize.width - widthLeft;

		frame = new JFrame();
		// frame.setBounds(100, 100, 562, 458);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setUndecorated(true); // Hide bar Window
		// frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setBounds(0, 0, screenSize.width, (int) (screenSize.height * 0.95));
		frame.setVisible(true);

		panelLeft = new JPanel();
		// panelLeft.setBounds(0, 0, 1110, screenSize.height);
		panelLeft.setBounds(0, 0, widthLeft, (int) (screenSize.height * 0.95));
		frame.getContentPane().add(panelLeft);
		GridBagLayout gbl_panelLeft = new GridBagLayout();
		// gbl_panelLeft.columnWidths = new int[] { 0, 0, 0, 0 };
		// gbl_panelLeft.rowHeights = new int[] { 0, 0, 0, 0 };
		gbl_panelLeft.columnWeights = createArrayDouble();
		gbl_panelLeft.rowWeights = createArrayDouble();
		panelLeft.setLayout(gbl_panelLeft);

		// create List Panel Items
		createListPanelwithContents();

		panelRight = new JPanel();
		// panelRight.setBounds(1120, 0, 246, screenSize.height);
		panelRight.setBounds(xright, 0, widthRight, (int) (screenSize.height * 0.95));
		frame.getContentPane().add(panelRight);

		btnStartListening = new JButton("Ad-hoc");
		btnStartListening.setBounds(52, (int) (screenSize.height * 0.9), 144, 25);
		btnStartListening.setVisible(false);
		btnStartListening.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// Run Server listen.
				// startSocketListener();
				// start socket listen broadcast
				// startListenBroadcase();
				UtilsRouter.executeCommand(LoadCommand.loadShellInstallRouter());
			}
		});

		btn2 = new JButton("Normal");
		btn2.setBounds(52, (int) (screenSize.height * 0.8), 144, 25);
		btn2.setVisible(false);
		btn2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				UtilsRouter.executeCommand(LoadCommand.loadShellInstallNormal());
			}
		});

		panelRight.setLayout(null);

		lblServerStatus = new JLabel("Server has stopped");
		lblServerStatus.setHorizontalAlignment(SwingConstants.CENTER);
		lblServerStatus.setFont(new Font("Dialog", Font.PLAIN, (int) (14 * ratio)));
		lblServerStatus.setBounds(53, 12, 143, 30);
		panelRight.add(lblServerStatus);
		panelRight.add(btnStartListening);
		panelRight.add(btn2);

		lblCurrenttime = new JLabel("00:00");
		lblCurrenttime.setHorizontalAlignment(SwingConstants.CENTER);
		lblCurrenttime.setFont(new Font("Dialog", Font.BOLD, (int) (60 * ratio)));
		lblCurrenttime.setBounds(22, 55, 212, 73);
		panelRight.add(lblCurrenttime);

		lblCurrentday = new JLabel("000 00/00/0000");
		lblCurrentday.setFont(new Font("Dialog", Font.BOLD, (int) (20 * ratio)));
		lblCurrentday.setHorizontalAlignment(SwingConstants.CENTER);
		lblCurrentday.setBounds(22, 128, 212, 40);
		panelRight.add(lblCurrentday);
	}

	// create array columnWeights and columnHeight
	private double[] createArrayDouble() {
		double[] d = { 1.0 }; // default has a value 1.0
		for (int i = 1; i < rows; i++) { // add number rows-1 value 1.0
			d = addElement(d, 1.0);
		}
		d = addElement(d, Double.MIN_VALUE); // add default value in end array.
		return d;
	}

	// Function add element to array double
	private double[] addElement(double[] a, double e) {
		a = Arrays.copyOf(a, a.length + 1);
		a[a.length - 1] = e;
		return a;
	}

	// create Panel
	private void createListPanelwithContents() {
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < col; c++) {
				String name_note = "note" + c + r;
				JPanel note = new JPanel();
				note.setName(name_note);
				GridBagLayout gridbag = new GridBagLayout();
				GridBagConstraints constraints = new GridBagConstraints();
				constraints.insets = new Insets(5, 5, 10, 10);
				constraints.weightx = GridBagConstraints.BOTH;
				constraints.weighty = GridBagConstraints.BOTH;
				constraints.fill = GridBagConstraints.BOTH;
				constraints.gridx = c;
				constraints.gridy = r;
				note.setFont(new Font("SansSerif", Font.PLAIN, (int) (14 * ratio)));
				note.setLayout(gridbag);
				panelLeft.add(note, constraints);

				// Set label...
				constraints.insets = new Insets(0, 0, 0, 0);
				constraints.fill = GridBagConstraints.NONE;
				constraints.gridx = GridBagConstraints.RELATIVE;
				constraints.gridy = GridBagConstraints.RELATIVE;
				constraints.gridwidth = 1;
				constraints.gridheight = 2;
				constraints.weighty = 0;
				constraints.weightx = 0;
//				URL url = getClass().getResource("/resources/ic_homemms.png");
				
				ImageIcon imageIcon = ScaleImage(ContantsHomeMMS.PiFolder + "/" + "ic_homemms.png");
				ImagePanel imagePanel = new ImagePanel(imageIcon.getImage());
				imagePanel.setLayout(gridbag);
				JPanel pnAvatar = new JPanel();
				pnAvatar.add(imagePanel);
				pnAvatar.setVisible(false);
				note.add(pnAvatar, constraints);

				constraints.weighty = 0;
				constraints.gridwidth = GridBagConstraints.REMAINDER;
				constraints.gridheight = 1;
				constraints.weightx = 1;
				constraints.fill = GridBagConstraints.BOTH;
				lblSender = new JLabel("Sender");
				lblSender.setForeground(Color.WHITE);
				lblSender.setFont(new Font("Dialog", Font.BOLD, (int) (14 * ratio)));
				JPanel pnSender = new JPanel();
				pnSender.add(lblSender);
				pnSender.setVisible(false);
				pnSender.setBackground(Color.blue);
				note.add(pnSender, constraints);

				constraints.fill = GridBagConstraints.HORIZONTAL;
				lblTitle = new JLabel("Title");
				lblTitle.setForeground(Color.WHITE);
				lblTitle.setFont(new Font("Dialog", Font.BOLD, (int) (14 * ratio)));
				lblTitle.setLayout(gridbag);
				JPanel pnTitle = new JPanel();
				pnTitle.add(lblTitle);
				pnTitle.setVisible(false);
				pnTitle.setBackground(Color.red);
				note.add(pnTitle, constraints);

				constraints.fill = GridBagConstraints.BOTH;
				constraints.gridwidth = 0;
				constraints.gridheight = 1;
				constraints.weighty = 1;
				tAMessage = new JTextArea();
				tAMessage.setEditable(false);
				tAMessage.setLineWrap(true);
				tAMessage.setWrapStyleWord(true);
				tAMessage.setFont(new Font("Dialog", Font.PLAIN, (int) (15 * ratio)));
				tAMessage.setLayout(gridbag);
				JScrollPane pnContent = new JScrollPane(tAMessage);
				pnContent.setVisible(false);
				note.add(pnContent, constraints);

				constraints.gridwidth = GridBagConstraints.REMAINDER;
				constraints.gridheight = 1;
				constraints.weighty = 0;
				lblTime = new JLabel("00:00 00 00/00/0000");
				lblTime.setFont(new Font("Dialog", Font.BOLD, (int) (14 * ratio)));
				lblTime.setLayout(gridbag);
				JPanel pnTime = new JPanel();
				pnTime.add(lblTime);
				pnTime.setVisible(false);
				note.add(pnTime, constraints);

				// Add note to List JPanels
				jPanels.add(note);
			}
		}
	}

	// Update info lable, textArea into Panel
	private void updateMessageForListPanel(List<JPanel> jPanels, final List<Message> messages, boolean delete) {
		if (delete) {
			HideAllMessage();
		}
		for (int i = 0; i < jPanels.size(); i++) {
			if (messages.size() > i) {
				JPanel pnAvatar = (JPanel) jPanels.get(i).getComponent(0);
				JPanel pnSender = (JPanel) jPanels.get(i).getComponent(1);
				JPanel pnTitle = (JPanel) jPanels.get(i).getComponent(2);
				JScrollPane spnContent = (JScrollPane) jPanels.get(i).getComponent(3);
				JPanel pnTime = (JPanel) jPanels.get(i).getComponent(4);

				if (pnAvatar != null) {
					pnAvatar.setVisible(true);
					ImagePanel imgPanel = (ImagePanel) pnAvatar.getComponent(0);
					User sender = messages.get(i).getSender();
			
					String avatar = ContantsHomeMMS.AppFolder + "/" + sender.getId() + "/" + sender.getAvatar();
					if (!UtilsMain.checkFilsIsExits(avatar)) {
//						URL url = getClass().getResource("/resources/ic_homemms.png"); 
						avatar = ContantsHomeMMS.PiFolder + "/" + "ic_homemms.png";
					}
					ImageIcon imageIcon = ScaleImage(avatar);
					imgPanel.setImage(imageIcon.getImage());
				}

				if (pnSender != null) {
					pnSender.setVisible(true);
					JLabel lbSender = (JLabel) pnSender.getComponent(0);
					lbSender.setText(messages.get(i).getSender().getNameDisplay());
					lbSender.setAlignmentX(Component.LEFT_ALIGNMENT);
				}

				if (pnTitle != null) {
					pnTitle.setVisible(true);
					JLabel lbTitle = (JLabel) pnTitle.getComponent(0);
					lbTitle.setText(messages.get(i).getTitle());
					lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
				}
				if (spnContent != null) {
					spnContent.setVisible(true);
					JTextArea tAMessage = (JTextArea) spnContent.getViewport().getView();
					tAMessage.setText(messages.get(i).getContentText());
					tAMessage.setAlignmentX(Component.LEFT_ALIGNMENT);
				}
				if (pnTime != null) {
					pnTime.setVisible(true);
					JLabel lbTime = (JLabel) pnTime.getComponent(0);
					lbTime.setText(Utils.convertStringToDate(messages.get(i).getTimestamp()));
					lbTime.setAlignmentX(Component.LEFT_ALIGNMENT);
				}

				// Action click on JPanel.
				addMouseListener(jPanels.get(i), messages.get(i));
			}
		}
	}

	private void HideAllMessage() {
		for (int i = 0; i < jPanels.size(); i++) {
			JPanel pnAvatar = (JPanel) jPanels.get(i).getComponent(0);
			pnAvatar.setVisible(false);
			JPanel pnSender = (JPanel) jPanels.get(i).getComponent(1);
			pnSender.setVisible(false);
			JPanel pnTitle = (JPanel) jPanels.get(i).getComponent(2);
			pnTitle.setVisible(false);
			JScrollPane spnContent = (JScrollPane) jPanels.get(i).getComponent(3);
			spnContent.setVisible(false);
			JPanel pnTime = (JPanel) jPanels.get(i).getComponent(4);
			pnTime.setVisible(false);
		}
	}

	private void addMouseListener(final JPanel jPanel, final Message message) {
		jPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				super.mouseEntered(e);
				Cursor cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
				jPanel.setCursor(cursor);
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				super.mousePressed(e);
				System.out.println(message.getmId());
			}
		});
	}

	// Show message from database in Desktop
	private void ShowMessage(boolean delete) {
		List<Message> messages = new ArrayList<Message>();
		messages = messageModel.get16Message();
		updateMessageForListPanel(jPanels, messages, delete);
	}

	public void UpdateMessage(final boolean delete) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				ShowMessage(delete);
			}
		});
	}

	private void UpdateTime() {
		Timer timer = new Timer(30000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DateFormat dateFormat = new SimpleDateFormat("HH:mm"); // 12:00
				dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
				DateFormat dateFormat2 = new SimpleDateFormat("EEE dd/MM/yyyy");// Sun
																				// 10/11/2015
				dateFormat2.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
				Date date = new Date();
				lblCurrenttime.setText(dateFormat.format(date));
				lblCurrentday.setText(dateFormat2.format(date));
			}
		});
		timer.setRepeats(true);
		timer.setCoalesce(true);
		timer.setInitialDelay(0);
		timer.start();
	}

	private void startSocketListener() {
		// start socket listen command
		server = new Server(port, this);
		new ServerRunning().start();
		lblServerStatus.setText("<html>Server is listening <br/>" + getIPOfServer() + ":" + port + "</html>");
	}

	// Reset status for all user - Set status for all user.
	public void ResetStatusForAllUser() {
		userModel.UpdateStatusAllUser(ContantsHomeMMS.UserStatus.offline.name());
	}

	// check if not exit tables then create it.
	private void FirstCheckDatabase() {
		if (!databaseHandler.checkTableExits(databaseHandler.TABLE_MESSAGE)) {
			databaseHandler.createTableMessage();
		}
		if (!databaseHandler.checkTableExits(databaseHandler.TABLE_USER)) {
			databaseHandler.createTableUser();
		}
	}

	class ServerRunning extends Thread {
		public void run() {
			server.StartSocket();
		}
	}

	private void startListenBroadcase() {
		Thread discoveryThread = new Thread(DiscoveryThread.getInstance());
		discoveryThread.start();
	}

	private void createFolderApp() {
		UtilsMain.createFolder(ContantsHomeMMS.AppFolder);
		UtilsMain.createFolder(ContantsHomeMMS.AppCacheFolder);
	}

	private void startDectectNetworkProblem() {
		Thread thread = new DectectNetworkProblem();
		thread.start();
	}

	private String getIPOfServer() {
		String ip = null;
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface iface = interfaces.nextElement();
				// filters out 127.0.0.1 and inactive interfaces
				if (iface.isLoopback() || !iface.isUp())
					continue;

				Enumeration<InetAddress> addresses = iface.getInetAddresses();
				while (addresses.hasMoreElements()) {
					InetAddress addr = addresses.nextElement();
					ip = addr.getHostAddress();
					System.out.println(iface.getDisplayName() + " " + ip);
				}
			}
		} catch (SocketException e) {
			throw new RuntimeException(e);
		}
		return ip;
	}

	private void setIcon() {
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/ic_homemms.png")));
	}

	private ImageIcon ScaleImage(String path) {
		if (path != null) {
			ImageIcon imageIcon = new ImageIcon(path);
			Image image = imageIcon.getImage();
			Image newimg = image.getScaledInstance((int) (45 * ratio), (int) (45 * ratio), java.awt.Image.SCALE_SMOOTH);
			ImageIcon newIcon = new ImageIcon(newimg);
			return newIcon;
		}
		return null;
	}

	class ImagePanel extends JPanel {

		private Image img;

		public ImagePanel(String img) {
			this(new ImageIcon(img).getImage());
		}

		public ImagePanel(Image img) {
			this.img = img;
			Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
			setPreferredSize(size);
			setMinimumSize(size);
			setMaximumSize(size);
			setSize(size);
			setLayout(null);
		}

		public void paintComponent(Graphics g) {
			g.drawImage(img, 0, 0, null);
		}

		public void setImage(String img) {
			this.img = new ImageIcon(img).getImage();
		}

		public void setImage(Image img) {
			this.img = img;
		}
	}
}
