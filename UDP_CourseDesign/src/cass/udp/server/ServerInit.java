package cass.udp.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import cass.udp.base.BaseParser;
import cass.udp.base.ExchangeBase;
import cass.udp.base.GlobalData;
import cass.udp.socket.UDPSerializable;

public class ServerInit {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		Scanner sc = new Scanner(System.in);
		System.out.println("Server Port:");
		int port = sc.nextInt();

		// read
		File users = new File("C:\\users.ser");
		File record = new File("C:\\record.ser");
		if (users.exists()) {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
					users));
			ServerCommon.users = (List<User>) ois.readObject();
			ois.close();
			System.out.println("loaded following users:");
			for (User u : ServerCommon.users) {
				System.out.println(u.name + "\t" + u.md5pwd);
			}
		} else {
			ServerCommon.users = new ArrayList<User>();
		}
		if (record.exists()) {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
					record));
			ServerCommon.records = (List<Record>) ois.readObject();
			ois.close();
			System.out.println("loaded following records:");
			for (Record r : ServerCommon.records) {
				System.out.println(r.content);
			}
		} else {
			ServerCommon.records = new ArrayList<Record>();
		}

		Method callback = GlobalData.class.getDeclaredMethod("serverOperate",
				Object.class, InetAddress.class);
		GlobalData.receiver = new UDPSerializable(port, null, callback);
		GlobalData.parser = BaseParser.getServerParser();
		GlobalData.receiver.start();

		System.out
				.println("Server successfully launched......\nPress Return to terminate.");

		while (true) {
			sc.nextLine();
			String s = sc.nextLine();
			if (s.trim().equals("")) {
				break;
			}
			if (s.equals("show users")) {
				for (User u : ServerCommon.users) {
					System.out.println(u.name);
				}
			} else if (s.equals("show online users")) {
				for (IPPort ipp : ServerCommon.onlineMap.keySet()) {
					System.out.println(ipp.ip + "\t" + ipp.port + "\t"
							+ ServerCommon.onlineMap.get(ipp));
				}
			} else if (s.startsWith("delete user ")) {
				String name = s.substring(12);
				boolean success = false;
				for (int i = 0; i < ServerCommon.users.size(); ++i) {
					User u = ServerCommon.users.get(i);
					if (u.name.equals(name)) {
						ServerCommon.users.remove(i);
						Iterator<IPPort> it = ServerCommon.onlineMap.keySet()
								.iterator();
						while (it.hasNext()) {
							IPPort ipp = it.next();
							if (ServerCommon.onlineMap.get(ipp).name
									.equals(name)) {
								ExchangeBase base = new ExchangeBase(
										"disconnect");
								UDPSerializable.send(base, ipp.ip, ipp.port);
								break;
							}
						}
						success = true;
						System.out.println("success");
						break;
					}
				}
				if (!success) {
					System.out.println("user not found");
				}
			}
		}

		System.out.println("TERMINATING LISTENING...");

		// terminate
		GlobalData.disconnect();
		sc.close();

		// save
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
				users));
		oos.writeObject(ServerCommon.users);
		oos.close();

		oos = new ObjectOutputStream(new FileOutputStream(record));
		oos.writeObject(ServerCommon.records);
		oos.close();

		Thread.sleep(5000);

		System.out.println("Server successfully terminated.");
	}

}
