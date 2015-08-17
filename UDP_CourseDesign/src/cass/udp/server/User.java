package cass.udp.server;

import java.io.Serializable;

public class User implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3108243570920301625L;
	public String name;
	public String md5pwd;
}
