package com.odde;

import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPTextMsgSenderApp extends UDPMsgSenderApp {
	
	private PrintStream out;
	private UDPMsgSender sender;
	private byte[] receiveData = new byte[1024];
	
	public UDPTextMsgSenderApp(PrintStream out, DatagramSocket socket) {
		this.out = out;
		sender = new UDPMsgSender(socket);
	}

	public void execute(String hostName, int portNumber, String message) throws IOException {
		sender.send(sender.createTextMessagePacket(hostName, portNumber, message));
		out.println(sender.getResponse(new DatagramPacket(receiveData, receiveData.length)));
		sender.close();
	}
}
