package com.odde;

import java.io.PrintStream;
import java.net.DatagramSocket;

public class UDPMsgSenderAppFactory {

	public static UDPMsgSenderApp createApp(String appType, PrintStream out, DatagramSocket socket) {
		if (appType.equals("-t")) 
			return new UDPTextMsgSenderApp(out, socket);
		else if (appType.equals("-h")) 
			return new UDPHexMsgSenderApp(out, socket);
		else
			return new InvalidSenderApp(out);
	}

}
