package com.thesis;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import Socket.Server;

public class ServerGUI {
	Button btnScan, btnClear, btnStartSocket;
	Label lblDeviceInfo, lblIP, lblIP_, lblName, lblName_, lblMac, lblMac_,
			lblMessages, lblSendTo, lblSendTo_, lblAttachFiles,
			lblAttachFiles_;
	static Label lblStatus;
	Group group;
	ProgressBar progressBar;

	protected Shell shell;
	private Text tMessages;
	private static Server server = null;
	private static int port = 2222;

	public ServerGUI() {
	}

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ServerGUI window = new ServerGUI();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(450, 370);
		shell.setText("Server Rasp Pi");

		btnScan = new Button(shell, SWT.NONE);
		btnScan.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
			}
		});
		btnScan.setBounds(10, 296, 75, 25);
		btnScan.setText("Scan");

		btnClear = new Button(shell, SWT.NONE);
		btnClear.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				onClear();
			}
		});
		btnClear.setBounds(91, 296, 75, 25);
		btnClear.setText("Clear");

		btnStartSocket = new Button(shell, SWT.NONE);
		btnStartSocket.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				startSocketListener();
			}
		});
		btnStartSocket.setBounds(172, 296, 75, 25);
		btnStartSocket.setText("Start Listen");

		lblDeviceInfo = new Label(shell, SWT.NONE);
		lblDeviceInfo.setBounds(10, 10, 65, 15);
		lblDeviceInfo.setText("Device Info");

		progressBar = new ProgressBar(shell, SWT.NONE);
		progressBar.setBounds(10, 285, 414, 5);

		group = new Group(shell, SWT.NONE);
		group.setBounds(0, 31, 347, 248);

		lblIP = new Label(group, SWT.NONE);
		lblIP.setBounds(10, 10, 20, 15);
		lblIP.setText("IP:");

		lblIP_ = new Label(group, SWT.NONE);
		lblIP_.setText("0.0.0.0");
		lblIP_.setBounds(59, 10, 168, 15);

		lblMac = new Label(group, SWT.NONE);
		lblMac.setBounds(10, 31, 55, 15);
		lblMac.setText("Mac:");

		lblName = new Label(group, SWT.NONE);
		lblName.setBounds(10, 52, 55, 15);
		lblName.setText("Name:");

		lblMac_ = new Label(group, SWT.NONE);
		lblMac_.setText("00:00:00:00:00:00");
		lblMac_.setBounds(59, 31, 168, 15);

		lblName_ = new Label(group, SWT.NONE);
		lblName_.setText("None");
		lblName_.setBounds(59, 52, 168, 15);

		lblMessages = new Label(group, SWT.NONE);
		lblMessages.setBounds(10, 94, 69, 15);
		lblMessages.setText("Messages:");

		tMessages = new Text(group, SWT.BORDER);
		tMessages.setBounds(10, 115, 327, 29);

		lblSendTo = new Label(group, SWT.NONE);
		lblSendTo.setBounds(10, 73, 55, 15);
		lblSendTo.setText("Send to:");

		lblSendTo_ = new Label(group, SWT.NONE);
		lblSendTo_.setBounds(69, 73, 168, 15);

		lblAttachFiles = new Label(group, SWT.NONE);
		lblAttachFiles.setBounds(10, 189, 69, 15);
		lblAttachFiles.setText("Attach Files:");

		lblAttachFiles_ = new Label(group, SWT.NONE);
		lblAttachFiles_.setBounds(85, 189, 252, 29);

		lblStatus = new Label(shell, SWT.NONE);
		lblStatus.setAlignment(SWT.CENTER);
		lblStatus.setBounds(128, 10, 197, 15);
		lblStatus.setText("Stop");
	}
	
	public void onClear(){
		lblAttachFiles_.setText("");
		lblIP_.setText("");
		lblMac_.setText("");
		lblName_.setText("");
		lblSendTo_.setText("");
		tMessages.setText("");
		lblStatus.setText("Server is listening");
	}

	private void startSocketListener() {
		server = new Server(port, this);
		new ServerRunning().start();
		lblStatus.setText("Server is listening");
	}

	public Label getlblIP_() {
		return lblIP_;
	}

	public Label getlblName_() {
		return lblName_;
	}

	public Label getlblMac_() {
		return lblMac_;
	}

	public Text getMessages() {
		return tMessages;
	}

	public Label getlblStatus() {
		return lblStatus;
	}

	public void updateInfo(final String IP, final String Name, final String Mac) {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				lblStatus.setText("Connected to " + Name);
				lblIP_.setText(IP);
				lblName_.setText(Name);
				lblMac_.setText(Mac);
			}
		});
	}
	
	public void updateReciever(final String msg) {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				lblSendTo_.setText(msg);
			}
		});
	}
	
	public void updateMessages(final String msg) {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				tMessages.setText(msg);
			}
		});
	}
	
	public void updateFileName(final String msg) {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				//Demo file name.
				String s = msg.substring(msg.lastIndexOf("/") + 1);
				lblAttachFiles_.setText(lblAttachFiles_.getText() + " " + s);
			}
		});
	}
	
	public void updateOnClear() {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				onClear();
			}
		});
	}

	class ServerRunning extends Thread {
		public void run() {
			server.StartSocket();
		}
	}
}
