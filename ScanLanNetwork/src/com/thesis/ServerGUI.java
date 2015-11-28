package com.thesis;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import Database.DatabaseHandler;
import Database.MessageModel;
import Database.Utils;
import Model.Message;
import Network.DiscoveryThread;
import Socket.Server;

public class ServerGUI {

	private JFrame frame;
	private JLabel lblServerStatus, lblTime, lblCurrentday, lblCurrenttime;;

	private JButton btnStartListening;
	private JPanel panelLeft, panelRight;
	private int col = 4, rows = 4;
	private List<JPanel> jPanels;
	private JLabel lblSender, lblTitle;
	private JTextArea tAMessage;

	private static Server server = null;
	private static int port = 2222;
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
		UpdateTime();
		FirstCheckDatabase();
		ShowMessage();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		jPanels = new ArrayList<JPanel>();
		databaseHandler = new DatabaseHandler();
		messageModel = new MessageModel();

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		frame = new JFrame();
		// frame.setBounds(100, 100, 562, 458);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setUndecorated(true); // Hide bar Window
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setVisible(true);

		int widthLeft = (int) (screenSize.width * 0.82);
		int xright = (int) (screenSize.getWidth() * 0.82);
		int widthRight = screenSize.width - widthLeft;

		panelLeft = new JPanel();
		// panelLeft.setBounds(0, 0, 1110, screenSize.height);
		panelLeft.setBounds(0, 0, widthLeft, screenSize.height);
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
		panelRight.setBounds(xright, 0, widthRight, screenSize.height);
		frame.getContentPane().add(panelRight);

		btnStartListening = new JButton("Start Listening");
		btnStartListening.setBounds(52, 720, 144, 25);
		btnStartListening.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				startSocketListener();
				// start socket listen broadcast
				startListenBroadcase();
			}
		});
		panelRight.setLayout(null);

		lblServerStatus = new JLabel("Server has stopped");
		lblServerStatus.setBounds(53, 12, 143, 15);
		panelRight.add(lblServerStatus);
		panelRight.add(btnStartListening);

		lblCurrenttime = new JLabel("00:00");
		lblCurrenttime.setHorizontalAlignment(SwingConstants.CENTER);
		lblCurrenttime.setFont(new Font("Dialog", Font.BOLD, 60));
		lblCurrenttime.setBounds(22, 39, 212, 73);
		panelRight.add(lblCurrenttime);

		lblCurrentday = new JLabel("000 00/00/0000");
		lblCurrentday.setFont(new Font("Dialog", Font.BOLD, 20));
		lblCurrentday.setHorizontalAlignment(SwingConstants.CENTER);
		lblCurrentday.setBounds(22, 112, 212, 40);
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
				note.setLayout(new BoxLayout(note, BoxLayout.Y_AXIS));
				// note.setVisible(false);
				GridBagConstraints gbc = new GridBagConstraints();
				gbc.insets = new Insets(5, 5, 10, 10);
				gbc.fill = GridBagConstraints.BOTH;
				gbc.gridx = c;
				gbc.gridy = r;
				panelLeft.add(note, gbc);

				// Set label...
				lblSender = new JLabel("Sender");
				note.add(lblSender);

				lblTitle = new JLabel("Title");
				note.add(lblTitle);

				tAMessage = new JTextArea();
				tAMessage.setEditable(false);
				note.add(tAMessage);

				lblTime = new JLabel("00:00 00 00/00/0000");
				note.add(lblTime);

				// Add note to List JPanels
				jPanels.add(note);
			}
		}
	}

	// Update info lable, textArea into Panel
	private void updateMessageForListPanel(List<JPanel> jPanels, final List<Message> messages) {
		for (int i = 0; i < jPanels.size(); i++) {
			if (messages.size() > i) {
				// jPanels.get(i).setVisible(true);
				JLabel lbSender = (JLabel) jPanels.get(i).getComponent(0);
				JLabel lbTitle = (JLabel) jPanels.get(i).getComponent(1);
				JTextArea tAMessage = (JTextArea) jPanels.get(i).getComponent(2);
				JLabel lbTime = (JLabel) jPanels.get(i).getComponent(3);

				if (lbSender != null) {
					lbSender.setText(messages.get(i).getSender().getNameDisplay());
					lbSender.setAlignmentX(Component.LEFT_ALIGNMENT);
				}
				if (lbTitle != null) {
					lbTitle.setText(messages.get(i).getTitle());
					lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
				}
				if (tAMessage != null) {
					tAMessage.setText(messages.get(i).getContentText());
					tAMessage.setAlignmentX(Component.LEFT_ALIGNMENT);
				}
				if (lbTime != null) {
					lbTime.setText(Utils.convertStringToDate(messages.get(i).getTimestamp()));
					lbTime.setAlignmentX(Component.LEFT_ALIGNMENT);
				}

				// Action click on JPanel.
				addMouseListener(jPanels.get(i), messages.get(i));
			}
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
	private void ShowMessage() {
		List<Message> messages = new ArrayList<Message>();
		messages = messageModel.get12Message();
		updateMessageForListPanel(jPanels, messages);
	}

	private void UpdateTime() {
		Timer timer = new Timer(30000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DateFormat dateFormat = new SimpleDateFormat("HH:mm"); // 12:00
				DateFormat dateFormat2 = new SimpleDateFormat("EEE dd/MM/yyyy");// Sun
																				// 10/11/2015

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
		lblServerStatus.setText("Server is listening");
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
}
