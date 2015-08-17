package cass.udp.socket;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;

public class UDPThread {
	private DatagramSocket socket;
	private Object target;
	private Method callback;
	private Method error;

	public UDPThread(int port, Object target, Method callback, Method error)
			throws Exception {
		socket = new DatagramSocket(port);
		socket.setSoTimeout(5000);
		this.callback = callback;
		this.target = target;
		this.error = error;
	}

	public void run() {
		DatagramPacket p = new DatagramPacket(new byte[65535], 65535);
		try {
			socket.receive(p);
			callback.invoke(target, p);
		} catch (Exception e) {
			try {
				error.invoke(target, e);
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e1) {
				e1.printStackTrace();
			}
			if (!(e instanceof SocketTimeoutException))
				e.printStackTrace();
		}
	}

	public void end() {
		socket.close();
	}
}
