package cass.udp.server;

import java.io.Serializable;
import java.util.Date;

public class Record implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7688273985830905090L;
	public User user;
	public String content;
	public Date date;
}
