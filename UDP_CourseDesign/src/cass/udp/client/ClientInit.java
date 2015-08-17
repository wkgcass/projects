package cass.udp.client;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import cass.udp.base.BaseParser;
import cass.udp.base.GlobalData;

public class ClientInit {

	public static void main(String[] args) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			UnsupportedLookAndFeelException {
		UIManager
				.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		GlobalData.parser = BaseParser.getClientParser();

		GlobalData.chat = new ChatFrame();
		GlobalData.dialog = new OnlinePropsDialog();
		GlobalData.frame = new WelcomeFrame();

		GlobalData.frame.setVisible(true);
	}

}
