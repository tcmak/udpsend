package com.odde;

import java.io.IOException;
import java.net.DatagramSocket;

public class UDPMsgSenderMain {
	public static void main(String args[]) throws IOException {
		if (args.length < 4) {
			System.out.println(UDPMsgSenderApp.USAGE_DESCRIPTION);
			return;
		}
		
		DatagramSocket clientSocket = new DatagramSocket();
		UDPMsgSenderApp senderApp = UDPMsgSenderAppFactory.createApp(args[2], System.out, clientSocket);
		((UDPTextMsgSenderApp)senderApp).execute(args[0], Integer.parseInt(args[1]), args[3]);
	}
}
