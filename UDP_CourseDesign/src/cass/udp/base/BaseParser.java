package cass.udp.base;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import cass.udp.socket.UDPSerializable;

public abstract class BaseParser {

	public abstract Map<String, Object> operate(ExchangeBase base)
			throws Exception;

	public static BaseParser getServerParser() {
		return new ServerParser();
	}

	public static BaseParser getClientParser() {
		return new ClientParser();
	}

	public static class ServerParser extends BaseParser {
		@Override
		public Map<String, Object> operate(ExchangeBase base) throws Exception {
			Map<String, Object> ret = new HashMap<String, Object>();
			ret.put("ip", InetAddress.getByName(base.getInfo().get("ip")));
			ret.put("port", Integer.parseInt(base.getInfo().get("port")));
			String method = base.getMethod();
			if (method.equals("connect_request")) {
				ret.put("user", base.getInfo().get("user"));
				ret.put("md5pwd", base.getInfo().get("md5pwd"));
			} else if (method.equals("register_request")) {
				ret.put("user", base.getInfo().get("user"));
				ret.put("md5pwd", base.getInfo().get("md5pwd"));
			} else if (method.equals("msg_deliver")) {
				ret.put("date", base.getInfo().get("date"));
				ret.put("msg", base.getInfo().get("msg"));
			}

			return ret;
		}
	}

	public static class ClientParser extends BaseParser {

		@Override
		public Map<String, Object> operate(ExchangeBase base) throws Exception {
			Map<String, Object> ret = new HashMap<String, Object>();
			String method = base.getMethod();
			if (method.equals("connect_request")) {
				InetAddress ip = InetAddress
						.getByName(base.getInfo().get("ip"));
				int port = Integer.parseInt(base.getInfo().get("port"));
				ret.put("ip", ip);
				ret.put("port", port);
				ExchangeBase exbase = new ExchangeBase("connect_response");
				UDPSerializable.send(exbase, ip, port);
			} else if (method.equals("connect_response")) {
				ret.put("disconnected", false);
			} else if (method.equals("msg_deliver")) {
				String msg = base.getInfo().get("msg");
				String date = base.getInfo().get("date");
				String name = base.getInfo().get("name");
				ret.put("msg", msg);
				ret.put("date", date);
				ret.put("name", name);
			} else if (method.equals("disconnect")) {
				ret.put("disconnected", true);
			} else if (method.equals("show_online_user_respond")) {
				ret.put("onlineMap", base.getEx().get(0));
				ret.put("records", base.getEx().get(1));
			} else if (method.equals("register_response")) {
				ret.put("error", base.getInfo().get("error"));
			}
			return ret;
		}
	}
}
