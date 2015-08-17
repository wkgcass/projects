package cass.udp.socket;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPSerializable extends Thread {

	private Object target;
	private Method callback;
	private UDPThread t;

	private boolean running = false;
	private boolean blocked = true;

	public UDPSerializable(int port, Object target, Method callback)
			throws Exception {
		t = new UDPThread(port, this, UDPSerializable.class.getDeclaredMethod(
				"callback", DatagramPacket.class),
				UDPSerializable.class.getDeclaredMethod("errorBack",
						Exception.class));
		this.target = target;
		this.callback = callback;

	}

	@Override
	public void run() {
		running = true;
		blocked = false;
		while (running) {
			while (blocked) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			blocked = true;
			if (!running) {
				break;
			}
			t.run();
		}
		t.end();
	}

	public void callback(DatagramPacket p) throws Exception {
		ByteArrayInputStream bais = new ByteArrayInputStream(p.getData());
		ObjectInputStream ois = new ObjectInputStream(bais);
		Object o = ois.readObject();
		callback.invoke(target, o, p.getAddress());
		blocked = false;
	}

	public void errorBack(Exception e) {
		blocked = false;
	}

	public static void send(Object o, InetAddress ip, int port)
			throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(o);
		byte[] buf = baos.toByteArray();
		DatagramPacket packet = new DatagramPacket(buf, buf.length, ip, port);
		DatagramSocket socket = new DatagramSocket();
		socket.send(packet);
		socket.close();
	}

	public boolean isBlocked() {
		return blocked;
	}

	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

}
