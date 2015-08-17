package cass.udp.base;

import java.net.InetAddress;
import java.text.DateFormat;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import cass.udp.client.ChatFrame;
import cass.udp.client.OnlinePropsDialog;
import cass.udp.client.WelcomeFrame;
import cass.udp.server.IPPort;
import cass.udp.server.Record;
import cass.udp.server.ServerCommon;
import cass.udp.server.User;
import cass.udp.socket.UDPSerializable;

public class GlobalData {
	private GlobalData() {
	}

	public static boolean showMe;

	public static BaseParser parser;

	public static UDPSerializable receiver;

	public static InetAddress targetIP;

	public static int targetPort;

	public static int myPort;

	public static WelcomeFrame frame;

	public static ChatFrame chat;

	public static OnlinePropsDialog dialog;

	@SuppressWarnings("unchecked")
	public static void clientOperate(Object obj, InetAddress ipAddr)
			throws Exception {
		if (obj instanceof ExchangeBase) {
			ExchangeBase base = (ExchangeBase) obj;
			Map<String, Object> ret = parser.operate(base);
			if (base.getMethod().equals("connect_request")) {
				// targetIP = (InetAddress) ret.get("ip");
				targetIP = ipAddr;
				targetPort = (int) ret.get("port");
				frame.setVisible(false);
				chat.setVisible(true);
			} else if (base.getMethod().equals("connect_response")) {
				frame.setVisible(false);
				chat.setVisible(true);
			} else if (base.getMethod().equals("msg_deliver")) {
				String msg = (String) ret.get("msg");
				String date = (String) ret.get("date");
				String name = (String) ret.get("name");
				chat.addString(name + " " + date + "\n" + msg);
			} else if (base.getMethod().equals("disconnect")) {
				JOptionPane.showMessageDialog(null,
						"TARGET DISCONNECTED\nTERMINATING LISTENING....");
				disconnect();
				Thread.sleep(5500);
			} else if (base.getMethod().equals("show_online_user_respond")) {
				Map<IPPort, User> onlineMap = (Map<IPPort, User>) ret
						.get("onlineMap");
				List<Record> records = (List<Record>) ret.get("records");

				DefaultTableModel model = ((DefaultTableModel) GlobalData.dialog
						.getOnlineUserTable().getModel());
				for (int i = model.getRowCount() - 1; i >= 0; --i) {
					model.removeRow(i);
				}
				for (IPPort ipp : onlineMap.keySet()) {
					model.addRow(new Object[] { ipp.ip.getHostAddress(),
							ipp.port, onlineMap.get(ipp).name });
				}
				GlobalData.dialog.getHistoryTA().setText("HISTORY");
				for (Record r : records) {
					GlobalData.dialog.getHistoryTA().append(
							"\n\n"
									+ r.user.name
									+ " "
									+ DateFormat.getDateTimeInstance().format(
											r.date) + "\n" + r.content);
				}
			} else if (base.getMethod().equals("user_pwd_mismatch")) {
				JOptionPane.showMessageDialog(null, "user_pwd_mismatch");
			} else if (base.getMethod().equals("register_response")) {
				String error = (String) ret.get("error");
				if (null != error) {
					JOptionPane.showMessageDialog(null, error);
				} else {
					JOptionPane.showMessageDialog(null, "register succeeded !");
				}
				disconnect();
			}
		}
	}

	public static void serverOperate(Object obj, InetAddress ipAddr)
			throws Exception {
		if (obj instanceof ExchangeBase) {
			ExchangeBase base = (ExchangeBase) obj;
			Map<String, Object> ret = parser.operate(base);
			if (base.getMethod().equals("connect_request")) {
				// InetAddress ip = (InetAddress) ret.get("ip");
				InetAddress ip = ipAddr;
				int port = (int) ret.get("port");
				String user = (String) ret.get("user");
				String md5pwd = (String) ret.get("md5pwd");
				for (User u : ServerCommon.users) {
					if (u.name.equals(user) && u.md5pwd.equals(md5pwd)) {
						ExchangeBase exbase = new ExchangeBase(
								"connect_response");
						IPPort ipp = new IPPort();
						ipp.ip = ip;
						ipp.port = port;
						ServerCommon.onlineMap.put(ipp, u);

						UDPSerializable.send(exbase, ip, port);
						System.out.println("User " + user
								+ " Logged in, with ip=" + ip + ", port="
								+ port);
						return;
					}
				}
				ExchangeBase exbase = new ExchangeBase("user_pwd_mismatch");
				UDPSerializable.send(exbase, ip, port);
			} else if (base.getMethod().equals("register_request")) {
				String user = (String) ret.get("user");
				for (User u : ServerCommon.users) {
					if (u.name.equals(user)) {
						ExchangeBase exbase = new ExchangeBase(
								"register_response");
						exbase.getInfo().put("error", "user_already_exist");
						return;
					}
				}
				// InetAddress ip = (InetAddress) ret.get("ip");
				InetAddress ip = ipAddr;
				int port = (int) ret.get("port");
				String md5pwd = (String) ret.get("md5pwd");
				User u = new User();
				u.name = user;
				u.md5pwd = md5pwd;
				ServerCommon.users.add(u);
				ExchangeBase exbase = new ExchangeBase("register_response");
				exbase.getInfo().put("error", null);
				UDPSerializable.send(exbase, ip, port);
				System.out.println("User " + user + " registered, with ip="
						+ ip + ", port=" + port);
			} else if (base.getMethod().equals("show_online_user_request")) {
				ExchangeBase exbase = new ExchangeBase(
						"show_online_user_respond");
				// InetAddress ip = (InetAddress) ret.get("ip");
				InetAddress ip = ipAddr;
				int port = (int) ret.get("port");
				exbase.getEx().add(ServerCommon.onlineMap);
				exbase.getEx().add(ServerCommon.records);
				UDPSerializable.send(exbase, ip, port);
			} else if (base.getMethod().equals("msg_deliver")) {
				// InetAddress ip = (InetAddress) ret.get("ip");
				InetAddress ip = ipAddr;
				int port = (int) ret.get("port");
				IPPort p = new IPPort();
				p.ip = ip;
				p.port = port;
				User u = ServerCommon.onlineMap.get(p);
				ExchangeBase exbase = new ExchangeBase("msg_deliver");
				exbase.getInfo().put("name", u.name);
				exbase.getInfo().put("date", (String) ret.get("date"));
				exbase.getInfo().put("msg", (String) ret.get("msg"));
				Record r = new Record();
				r.content = (String) ret.get("msg");
				r.date = DateFormat.getDateTimeInstance().parse(
						(String) ret.get("date"));
				r.user = u;
				ServerCommon.records.add(r);
				for (IPPort ipp : ServerCommon.onlineMap.keySet()) {
					UDPSerializable.send(exbase, ipp.ip, ipp.port);
				}
			} else if (base.getMethod().equals("disconnect")) {
				// InetAddress ip = (InetAddress) ret.get("ip");
				InetAddress ip = ipAddr;
				int port = (int) ret.get("port");
				IPPort p = new IPPort();
				p.ip = ip;
				p.port = port;
				ServerCommon.onlineMap.remove(p);
			}
		}
	}

	public static void disconnect() {
		if (receiver != null) {
			receiver.setRunning(false);
			receiver.setBlocked(false);
		}
		receiver = null;
		targetIP = null;
		targetPort = 0;
		myPort = 0;
		if (null != chat) {
			chat.setVisible(false);
		}
		if (null != frame) {
			frame.setVisible(true);
		}
	}
}
