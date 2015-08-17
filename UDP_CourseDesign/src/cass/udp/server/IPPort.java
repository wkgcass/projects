package cass.udp.server;

import java.io.Serializable;
import java.net.InetAddress;

public class IPPort implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -10101074211747412L;
	public InetAddress ip;
	public int port;

	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (o == this)
			return true;
		if (o instanceof IPPort) {
			IPPort t = (IPPort) o;
			if (t.port == port
					&& t.ip.getHostAddress().equals(ip.getHostAddress()))
				return true;
			else
				return false;
		} else {
			return false;
		}
	}

	public int hashCode() {
		return ip.hashCode() + port;
	}
}
