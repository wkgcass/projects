package cass.udp.client;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;

import cass.udp.base.GlobalData;
import cass.udp.base.ExchangeBase;
import cass.udp.socket.UDPSerializable;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.lang.reflect.Method;
import java.net.InetAddress;

import javax.swing.JPasswordField;

public class WelcomeFrame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6011615185120883879L;
	private WelcomeFrame frame = this;
	private JPanel contentPane;
	private JTextField targetIPTF;
	private JTextField targetPortTF;
	private JTextField myPortTF;
	private JTextField serverIp;
	private JTextField serverPort;
	private JTextField userName;
	private JPasswordField pwd;
	private JTextField myPort;

	/**
	 * Create the frame.
	 */
	public WelcomeFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 610, 340);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(36, 42, 532, 209);
		contentPane.add(tabbedPane);

		JPanel serverPanel = new JPanel();
		tabbedPane.addTab("Connect Server", null, serverPanel, null);
		serverPanel.setLayout(null);

		serverIp = new JTextField();
		serverIp.setBounds(99, 27, 224, 21);
		serverPanel.add(serverIp);
		serverIp.setColumns(10);

		serverPort = new JTextField();
		serverPort.setBounds(404, 27, 66, 21);
		serverPanel.add(serverPort);
		serverPort.setColumns(10);

		userName = new JTextField();
		userName.setBounds(99, 58, 224, 21);
		serverPanel.add(userName);
		userName.setColumns(10);

		pwd = new JPasswordField();
		pwd.setBounds(99, 89, 224, 21);
		serverPanel.add(pwd);

		myPort = new JTextField();
		myPort.setBounds(404, 89, 66, 21);
		serverPanel.add(myPort);
		myPort.setColumns(10);

		JButton btnLogin = new JButton("LOGIN");
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GlobalData.showMe = false;
				ExchangeBase base = new ExchangeBase("connect_request");
				base.getInfo().put("user", userName.getText());
				base.getInfo().put("md5pwd", new String(pwd.getPassword()));
				try {
					// start to listen
					Method m;
					try {
						m = GlobalData.class.getDeclaredMethod("clientOperate",
								Object.class, InetAddress.class);
						if (GlobalData.receiver != null) {
							GlobalData.disconnect();
						}
						GlobalData.targetIP = InetAddress.getByName(serverIp
								.getText());
						GlobalData.targetPort = Integer.parseInt(serverPort
								.getText());
						GlobalData.myPort = Integer.parseInt(myPort.getText());
						GlobalData.receiver = new UDPSerializable(Integer
								.parseInt(myPort.getText()), null, m);
						GlobalData.receiver.start();
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(frame, e1);
					}

					base.getInfo().put("ip",
							InetAddress.getLocalHost().getHostAddress());
					base.getInfo().put("port", myPort.getText());
					UDPSerializable.send(base,
							InetAddress.getByName(serverIp.getText()),
							Integer.parseInt(serverPort.getText()));
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(frame, e1);
				}
			}
		});
		btnLogin.setBounds(99, 134, 93, 23);
		serverPanel.add(btnLogin);

		JButton btnRegister = new JButton("REGISTER");
		btnRegister.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ExchangeBase base = new ExchangeBase("register_request");
				base.getInfo().put("user", userName.getText());
				base.getInfo().put("md5pwd", new String(pwd.getPassword()));
				try {
					// start to listen
					Method m;
					try {
						m = GlobalData.class.getDeclaredMethod("clientOperate",
								Object.class, InetAddress.class);
						if (GlobalData.receiver != null) {
							GlobalData.disconnect();
						}
						GlobalData.receiver = new UDPSerializable(Integer
								.parseInt(myPort.getText()), null, m);
						GlobalData.receiver.start();
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(frame, e1);
					}

					base.getInfo().put("ip",
							InetAddress.getLocalHost().getHostAddress());
					base.getInfo().put("port", myPort.getText());
					UDPSerializable.send(base,
							InetAddress.getByName(serverIp.getText()),
							Integer.parseInt(serverPort.getText()));
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(frame, e1);
				}
			}
		});
		btnRegister.setBounds(230, 134, 93, 23);
		serverPanel.add(btnRegister);

		JLabel lblServerIp = new JLabel("Server IP");
		lblServerIp.setBounds(24, 30, 65, 15);
		serverPanel.add(lblServerIp);

		JLabel lblServerPort = new JLabel("Server Port");
		lblServerPort.setBounds(333, 30, 78, 15);
		serverPanel.add(lblServerPort);

		JLabel lblMyPort_1 = new JLabel("My Port");
		lblMyPort_1.setBounds(333, 92, 54, 15);
		serverPanel.add(lblMyPort_1);

		JLabel lblUserName = new JLabel("User Name");
		lblUserName.setBounds(24, 61, 62, 15);
		serverPanel.add(lblUserName);

		JLabel lblPassword = new JLabel("Password");
		lblPassword.setBounds(24, 92, 54, 15);
		serverPanel.add(lblPassword);

		JPanel c2cPanel = new JPanel();
		tabbedPane.addTab("Connect Client", null, c2cPanel, null);
		c2cPanel.setLayout(null);

		targetIPTF = new JTextField();
		targetIPTF.setBounds(93, 60, 277, 21);
		c2cPanel.add(targetIPTF);
		targetIPTF.setColumns(10);

		targetPortTF = new JTextField();
		targetPortTF.setBounds(380, 60, 66, 21);
		c2cPanel.add(targetPortTF);
		targetPortTF.setColumns(10);

		JLabel lblIp = new JLabel("IP");
		lblIp.setBounds(91, 36, 54, 15);
		c2cPanel.add(lblIp);

		JLabel lblPort = new JLabel("port");
		lblPort.setBounds(380, 36, 54, 15);
		c2cPanel.add(lblPort);

		JButton connectBtn = new JButton("CONNECT");
		connectBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GlobalData.showMe = true;
				try {
					// start to listen
					Method m;
					try {
						m = GlobalData.class.getDeclaredMethod("clientOperate",
								Object.class, InetAddress.class);
						if (GlobalData.receiver != null) {
							GlobalData.disconnect();
						}
						GlobalData.targetIP = InetAddress.getByName(targetIPTF
								.getText());
						GlobalData.targetPort = Integer.parseInt(targetPortTF
								.getText());
						GlobalData.myPort = Integer.parseInt(myPortTF.getText());
						GlobalData.receiver = new UDPSerializable(Integer
								.parseInt(myPortTF.getText()), null, m);
						GlobalData.receiver.start();
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(frame, e1);
					}

					ExchangeBase exbase = new ExchangeBase("connect_request");
					exbase.getInfo().put("ip",
							InetAddress.getLocalHost().getHostAddress());
					exbase.getInfo().put("port", myPortTF.getText());
					UDPSerializable.send(exbase,
							InetAddress.getByName(targetIPTF.getText()),
							Integer.parseInt(targetPortTF.getText()));
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(frame, e1);
				}
			}
		});
		connectBtn.setBounds(93, 129, 93, 23);
		c2cPanel.add(connectBtn);

		JButton listenBtn = new JButton("LISTEN");
		listenBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GlobalData.showMe = true;
				Method m;
				try {
					m = GlobalData.class.getDeclaredMethod("clientOperate",
							Object.class, InetAddress.class);
					GlobalData.myPort = Integer.parseInt(myPortTF.getText());
					if (GlobalData.receiver != null) {
						GlobalData.disconnect();
					}
					GlobalData.receiver = new UDPSerializable(Integer
							.parseInt(myPortTF.getText()), null, m);
					GlobalData.receiver.start();
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(frame, e1);
				}

			}
		});
		listenBtn.setBounds(353, 129, 93, 23);
		c2cPanel.add(listenBtn);

		myPortTF = new JTextField();
		myPortTF.setBounds(380, 98, 66, 21);
		c2cPanel.add(myPortTF);
		myPortTF.setColumns(10);

		JLabel lblMyPort = new JLabel("my port");
		lblMyPort.setBounds(313, 104, 54, 15);
		c2cPanel.add(lblMyPort);
	}
}
