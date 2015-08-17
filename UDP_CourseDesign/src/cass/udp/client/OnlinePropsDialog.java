package cass.udp.client;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

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

public class OnlinePropsDialog extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3266304693498666767L;

	private JTable onlineUserTable;

	private JTextArea historyTA;

	private JDialog d = this;

	/**
	 * Create the dialog.
	 */
	public OnlinePropsDialog() {
		setBounds(100, 100, 618, 712);
		getContentPane().setLayout(null);

		historyTA = new JTextArea();
		historyTA.setEditable(false);

		JScrollPane jsp1 = new JScrollPane(historyTA);
		jsp1.setBounds(10, 35, 582, 256);
		getContentPane().add(jsp1);

		JLabel lblHistory = new JLabel("History");
		lblHistory.setBounds(10, 10, 54, 15);
		getContentPane().add(lblHistory);

		JButton btnSave = new JButton("SAVE");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser();
				int result = jfc.showSaveDialog(d);
				if (result == JFileChooser.APPROVE_OPTION) {
					File f = jfc.getSelectedFile();
					try {
						PrintWriter pw = new PrintWriter(f);
						pw.print(historyTA.getText().replace("\n", "\n\r"));
						pw.close();
						JOptionPane.showMessageDialog(d, "SUCCESS !");
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		btnSave.setBounds(499, 6, 93, 23);
		getContentPane().add(btnSave);

		JLabel lblOnlineUsers = new JLabel("Online Users");
		lblOnlineUsers.setBounds(10, 301, 137, 15);
		getContentPane().add(lblOnlineUsers);

		String[] col = new String[] { "IP Address", "Port", "User Name" };
		DefaultTableModel md = new DefaultTableModel(new String[0][3], col);
		onlineUserTable = new JTable(md);

		JScrollPane jsp2 = new JScrollPane(onlineUserTable);
		jsp2.setBounds(10, 326, 582, 338);
		getContentPane().add(jsp2);

	}

	public JTable getOnlineUserTable() {
		return onlineUserTable;
	}

	public JTextArea getHistoryTA() {
		return historyTA;
	}

	@Override
	public void setVisible(boolean flag) {
		if (flag == true) {
			ExchangeBase o = new ExchangeBase("show_online_user_request");
			try {
				o.getInfo().put("ip",
						InetAddress.getLocalHost().getHostAddress());
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			}
			o.getInfo().put("port", new Integer(GlobalData.myPort).toString());
			try {
				UDPSerializable.send(o, GlobalData.targetIP,
						GlobalData.targetPort);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		super.setVisible(flag);
	}
}
