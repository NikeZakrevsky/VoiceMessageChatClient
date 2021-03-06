import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Font;

public class IP extends JDialog {

	private static final long serialVersionUID = 1L;
	private JTextField textField;
	private int clientPortForVoice = 3001;
	private int clientPortForMessage = 5001;
	private JTextField txtDefault;
	private static DatagramSocket socketForVoice;
	private static Thread thr1, thr2;

	public static void main1() {
		try {
			IP dialog = new IP();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public IP() throws SocketException {
		if (socketForVoice == null) {
			socketForVoice = new DatagramSocket(clientPortForVoice);
		}
		setBounds(100, 100, 307, 203);
		getContentPane().setLayout(null);

		JButton ConnectToServer = new JButton("Connect");
		ConnectToServer.setBounds(47, 131, 89, 23);
		getContentPane().add(ConnectToServer);
		

		textField = new JTextField();
		textField.setText("192.168.1.6");
		textField.setBounds(10, 30, 272, 20);
		getContentPane().add(textField);
		textField.setColumns(10);

		JLabel lblIp = new JLabel("Server Address:");
		lblIp.setBounds(10, 13, 136, 14);
		getContentPane().add(lblIp);

		JLabel lblNickname = new JLabel("Nickname:");
		lblNickname.setBounds(10, 68, 89, 14);
		getContentPane().add(lblNickname);

		txtDefault = new JTextField();
		txtDefault.setText("Default");
		txtDefault.setBounds(10, 86, 272, 20);
		getContentPane().add(txtDefault);
		txtDefault.setColumns(10);
		
		JLabel LabelIP = new JLabel("");
		LabelIP.setFont(new Font("Times New Roman", Font.PLAIN, 11));
		LabelIP.setForeground(Color.RED);
		LabelIP.setBounds(10, 50, 126, 14);
		getContentPane().add(LabelIP);
		JLabel LabelNick = new JLabel("");
		LabelNick.setForeground(Color.RED);
		LabelNick.setFont(new Font("Times New Roman", Font.PLAIN, 11));
		LabelNick.setBounds(10, 106, 126, 14);
		getContentPane().add(LabelNick);
		ConnectToServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				LabelIP.setText("");
				LabelNick.setText("");
				Pattern checkIPattern = Pattern.compile(
				        "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
				        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
				        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
				        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
					Matcher matchIP = checkIPattern.matcher(textField.getText());
					boolean IPCorrect = matchIP.matches();
					
					boolean NickCorrect = false;
					if(txtDefault.getText().length() < 10 && txtDefault.getText().length() != 0) {
						NickCorrect = true;
					}
					try {
						if(IPCorrect  && NickCorrect) {
							new Thread(new MicrophoneReader(socketForVoice, textField.getText(), txtDefault.getText())).start();
							new Thread(new PlaySound("connected.wav")).start();
							new Thread(new PlayVoice(socketForVoice)).start();
							try {
								new Thread(new InputMessage()).start();
							} catch (SocketException e) {
								e.printStackTrace();
							}
							IP.this.dispose();
						}
						else {
							if(!IPCorrect)
								LabelIP.setText("Wrong IP address");
							if(!NickCorrect)
								LabelNick.setText("Wrong Nickname");
						}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		JButton btnNewButton_1 = new JButton("Cancel");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				IP.this.dispose();
			}
		});
		btnNewButton_1.setBounds(158, 131, 89, 23);
		getContentPane().add(btnNewButton_1);
		

		
	}

	public static void stop() {
		thr1.interrupt();
		thr2.interrupt();
	}
}
