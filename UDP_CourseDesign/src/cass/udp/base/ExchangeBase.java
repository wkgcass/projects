package cass.udp.base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExchangeBase implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8981365117330326178L;
	private String method;
	private Map<String, String> info;
	private List<Object> ex;

	public ExchangeBase(String method) {
		this.method = method;
		this.info = new HashMap<String, String>();
		ex = new ArrayList<Object>();
	}

	public String getMethod() {
		return method;
	}

	public Map<String, String> getInfo() {
		return info;
	}

	public List<Object> getEx() {
		return ex;
	}

}
