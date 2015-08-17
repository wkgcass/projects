package cass.udp.client;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JLabel;

import cass.udp.base.ExchangeBase;
import cass.udp.base.GlobalData;
import cass.udp.socket.UDPSerializable;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.util.Date;

public class ChatFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7705612980616060274L;

	private JPanel contentPane;

	private ChatFrame chatFrame = this;

	private JTextArea sendTA;

	private JTextArea receiveTA;

	private JLabel targetIpLbl;

	private JLabel targetPortLbl;

	private JLabel myPortLbl;

	/**
	 * Create the frame.
	 */
	public ChatFrame() {
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 634, 449);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		sendTA = new JTextArea();
		sendTA.setBounds(10, 258, 598, 110);
		contentPane.add(sendTA);

		JButton btnSend = new JButton("SEND");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String msg = sendTA.getText();
				ExchangeBase exbase = new ExchangeBase("msg_deliver");
				exbase.getInfo().put("msg", msg);
				try {
					exbase.getInfo().put("name",
							InetAddress.getLocalHost().getHostAddress());
					exbase.getInfo().put("ip",
							InetAddress.getLocalHost().getHostAddress());
					exbase.getInfo().put("port",
							new Integer(GlobalData.myPort).toString());
				} catch (UnknownHostException e2) {
					e2.printStackTrace();
					exbase.getInfo().put("name", "?");
				}
				exbase.getInfo().put("date",
						DateFormat.getDateTimeInstance().format(new Date()));
				try {
					UDPSerializable.send(exbase, GlobalData.targetIP,
							GlobalData.targetPort);
					sendTA.setText("");
					if (GlobalData.showMe) {
						receiveTA.append("\n\n[Me]"
								+ DateFormat.getDateTimeInstance().format(
										new Date()) + "\n" + msg);
					}
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(chatFrame, e1);
				}
			}
		});
		btnSend.setBounds(515, 378, 93, 23);
		contentPane.add(btnSend);

		receiveTA = new JTextArea();
		receiveTA.setEditable(false);
		receiveTA.setLineWrap(true);
		receiveTA.setWrapStyleWord(true);
		receiveTA.setText("START CHATTING");
		receiveTA.setBounds(10, 32, 598, 196);

		JScrollPane jsp = new JScrollPane(receiveTA);
		jsp.setBounds(10, 32, 598, 196);
		contentPane.add(jsp);

		JLabel lblSendText = new JLabel("SEND TEXT");
		lblSendText.setBounds(10, 238, 105, 15);
		contentPane.add(lblSendText);

		JButton btnShowOnlineProps = new JButton("SHOW ONLINE PROPERTIES");
		btnShowOnlineProps.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GlobalData.dialog.setVisible(true);
			}
		});
		btnShowOnlineProps.setBounds(10, 378, 216, 23);
		contentPane.add(btnShowOnlineProps);

		JLabel lblReceiveText = new JLabel("RECEIVE TEXT");
		lblReceiveText.setBounds(10, 10, 145, 15);
		contentPane.add(lblReceiveText);

		JLabel lblTargetIp = new JLabel("target ip");
		lblTargetIp.setBounds(172, 10, 54, 15);
		contentPane.add(lblTargetIp);

		targetIpLbl = new JLabel("");
		targetIpLbl.setBounds(236, 10, 120, 15);
		contentPane.add(targetIpLbl);

		JLabel lblTargetPort = new JLabel("target port");
		lblTargetPort.setBounds(401, 10, 72, 15);
		contentPane.add(lblTargetPort);

		targetPortLbl = new JLabel("");
		targetPortLbl.setBounds(483, 10, 82, 15);
		contentPane.add(targetPortLbl);

		JLabel lblMyPort = new JLabel("my port");
		lblMyPort.setBounds(365, 382, 54, 15);
		contentPane.add(lblMyPort);

		myPortLbl = new JLabel("");
		myPortLbl.setBounds(429, 382, 54, 15);
		contentPane.add(myPortLbl);

		JButton btnDisconnect = new JButton("DISCONNECT");
		btnDisconnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ExchangeBase exbase = new ExchangeBase("disconnect");
				try {
					exbase.getInfo().put("ip",
							InetAddress.getLocalHost().getHostAddress());
					exbase.getInfo().put("port",
							new Integer(GlobalData.myPort).toString());
					UDPSerializable.send(exbase, GlobalData.targetIP,
							GlobalData.targetPort);
					GlobalData.disconnect();
					JOptionPane.showMessageDialog(chatFrame,
							"TERMINATING SOCKET LISTENING...");
					Thread.sleep(5500);
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(chatFrame, e1);
				}
			}
		});
		btnDisconnect.setBounds(236, 378, 93, 23);
		contentPane.add(btnDisconnect);

		JButton btnSave = new JButton("SAVE");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser();
				int result = jfc.showSaveDialog(chatFrame);
				if (result == JFileChooser.APPROVE_OPTION) {
					File f = jfc.getSelectedFile();
					try {
						PrintWriter out = new PrintWriter(f);
						out.print(receiveTA.getText().replace("\n", "\r\n"));
						out.flush();
						out.close();
						JOptionPane.showMessageDialog(chatFrame, "SUCCESS !");
					} catch (FileNotFoundException e1) {
						JOptionPane.showMessageDialog(chatFrame, e1);
					}

				}
			}
		});
		btnSave.setBounds(515, 234, 93, 23);
		contentPane.add(btnSave);
	}

	public void addString(String str) {
		receiveTA.append("\n\n" + str);
	}

	@Override
	public void setVisible(boolean flag) {
		if (flag == true) {
			targetIpLbl.setText(GlobalData.targetIP.getHostAddress());
			targetPortLbl
					.setText(new Integer(GlobalData.targetPort).toString());
			myPortLbl.setText(new Integer(GlobalData.myPort).toString());
			receiveTA.setText("START CHATTING");
		}
		super.setVisible(flag);
	}
}
