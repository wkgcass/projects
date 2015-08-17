package cass.udp.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerCommon {
	public static List<User> users;
	public static List<Record> records;
	public static Map<IPPort, User> onlineMap = new HashMap<IPPort, User>();
}
