package com.thesis;

import java.awt.Button;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;

import Socket.Server;

public class ServerGUI {

	private JFrame frame;
	private JLabel lblDeviceInfo, lblServerStatus, lblName, lblName_,
			lblIpaddr, lblIpaddr_, lblMac, lblMac_, lblReciever, lblReciever_,
			lblMessages, lblPhotoFiles, lblPhotoFiles_, lblAudioFiles,
			lblAudioFiles_, lblVideoFiles, lblVideoFiles_;
	private JTextArea taMessages_;
	private Button btnStartListening;
	JProgressBar progressBar;

	private static Server server = null;
	private static int port = 2222;

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
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 562, 458);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		lblDeviceInfo = new JLabel("Device Info");
		lblDeviceInfo.setBounds(10, 11, 78, 14);
		frame.getContentPane().add(lblDeviceInfo);

		lblServerStatus = new JLabel("Server has stopped");
		lblServerStatus.setBounds(221, 11, 312, 14);
		frame.getContentPane().add(lblServerStatus);

		lblName = new JLabel("Name:");
		lblName.setBounds(10, 36, 78, 14);
		frame.getContentPane().add(lblName);

		lblName_ = new JLabel("Unknow");
		lblName_.setBounds(94, 36, 140, 14);
		frame.getContentPane().add(lblName_);

		lblIpaddr = new JLabel("IPAddr:");
		lblIpaddr.setBounds(10, 61, 78, 14);
		frame.getContentPane().add(lblIpaddr);

		lblIpaddr_ = new JLabel("0.0.0.0");
		lblIpaddr_.setBounds(94, 61, 140, 14);
		frame.getContentPane().add(lblIpaddr_);

		lblMac = new JLabel("Mac:");
		lblMac.setBounds(10, 86, 78, 14);
		frame.getContentPane().add(lblMac);

		lblMac_ = new JLabel("00:00:00:00:00:00");
		lblMac_.setBounds(94, 86, 140, 14);
		frame.getContentPane().add(lblMac_);

		lblReciever = new JLabel("Reciever:");
		lblReciever.setBounds(10, 111, 78, 14);
		frame.getContentPane().add(lblReciever);

		lblReciever_ = new JLabel("Unknow");
		lblReciever_.setBounds(94, 111, 140, 14);
		frame.getContentPane().add(lblReciever_);

		lblMessages = new JLabel("Messages:");
		lblMessages.setBounds(10, 139, 78, 14);
		frame.getContentPane().add(lblMessages);

		taMessages_ = new JTextArea();
		taMessages_.setBounds(10, 172, 411, 139);
		frame.getContentPane().add(taMessages_);

		lblAudioFiles = new JLabel("Audio Files:");
		lblAudioFiles.setBounds(10, 331, 110, 14);
		frame.getContentPane().add(lblAudioFiles);

		lblVideoFiles = new JLabel("Video Files:");
		lblVideoFiles.setBounds(10, 356, 110, 14);
		frame.getContentPane().add(lblVideoFiles);

		lblPhotoFiles = new JLabel("Photo Files:");
		lblPhotoFiles.setBounds(10, 381, 110, 14);
		frame.getContentPane().add(lblPhotoFiles);

		lblAudioFiles_ = new JLabel("");
		lblAudioFiles_.setBounds(130, 331, 179, 14);
		frame.getContentPane().add(lblAudioFiles_);

		lblVideoFiles_ = new JLabel("");
		lblVideoFiles_.setBounds(130, 356, 179, 14);
		frame.getContentPane().add(lblVideoFiles_);

		lblPhotoFiles_ = new JLabel("");
		lblPhotoFiles_.setBounds(130, 381, 179, 14);
		frame.getContentPane().add(lblPhotoFiles_);

		progressBar = new JProgressBar();
		progressBar.setBounds(10, 406, 411, 5);
		frame.getContentPane().add(progressBar);

		btnStartListening = new Button("Start Listening");
		btnStartListening.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				startSocketListener();
			}
		});
		btnStartListening.setBounds(430, 356, 103, 39);
		frame.getContentPane().add(btnStartListening);
	}

	public void onClear() {
		lblAudioFiles_.setText("");
		lblPhotoFiles_.setText("");
		lblVideoFiles_.setText("");
		lblIpaddr_.setText("");
		lblMac_.setText("");
		lblName_.setText("");
		lblReciever_.setText("");
		taMessages_.setText("");
		lblServerStatus.setText("Server is listening");
	}

	private void startSocketListener() {
		server = new Server(port, this);
		new ServerRunning().start();
		lblServerStatus.setText("Server is listening");
	}

	public JLabel getlblIP_() {
		return lblIpaddr_;
	}

	public JLabel getlblName_() {
		return lblName_;
	}

	public JLabel getlblMac_() {
		return lblMac_;
	}

	public JTextArea getMessages() {
		return taMessages_;
	}

	public JLabel getlblStatus() {
		return lblServerStatus;
	}

	public void updateInfo(final String IP, final String Name, final String Mac) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				lblServerStatus.setText("Connected to " + Name);
				lblIpaddr_.setText(IP);
				lblName_.setText(Name);
				lblMac_.setText(Mac);
			}
		});
	}

	public void updateReciever(final String msg) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				lblReciever_.setText(msg);
			}
		});
	}

	public void updateMessages(final String msg) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				taMessages_.setText(msg);
			}
		});
	}

	public void updatePhotoFile(final String photofile) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				// Demo file name.
				String photo = photofile.substring(photofile.lastIndexOf("/") + 1);
				lblPhotoFiles_.setText(lblPhotoFiles_.getText() + " " + photo);
			}
		});
	}

	public void updateAudioFile(final String audioFile) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				// Demo file name.
				String audio = audioFile.substring(audioFile.lastIndexOf("/") + 1);
				lblAudioFiles_.setText(lblAudioFiles_.getText() + " " + audio);
			}
		});
	}

	public void updateVideoFile(final String videoFile) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				// Demo file name.
				String video = videoFile.substring(videoFile.lastIndexOf("/") + 1);
				lblVideoFiles_.setText(lblVideoFiles_.getText() + " " + video);
			}
		});
	}

	public void updateOnClear() {
		EventQueue.invokeLater(new Runnable() {
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
