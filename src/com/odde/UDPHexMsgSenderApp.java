package com.odde;

import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

public class UDPHexMsgSenderApp extends UDPMsgSenderApp{

	static public final String INVALID_HEX_STRING = "Invalid Hex String";
	private PrintStream out;
	private UDPMsgSender sender;
	private byte[] receiveData = new byte[1024];

	public UDPHexMsgSenderApp(PrintStream out, DatagramSocket socket) {
		this.out = out;
		sender = new UDPMsgSender(socket);
	}

	public void execute(String hostName, int port, byte[] message) throws IOException {
		sender.send(sender.createHexMessagePacket(hostName, port, message));
		out.println(sender.getResponse(new DatagramPacket(receiveData, receiveData.length)));
		sender.close();
	}

	@Override
	public void execute(String hostName, int port, String message) throws IOException {
		try {
			execute(hostName, port, Hex.decodeHex(distillHexString(message).toCharArray()));
		} catch (DecoderException e) {
			out.println(INVALID_HEX_STRING);
		}
	}

	private String distillHexString(String message) {
		String filteredString = "";
		
		for (int i=0; i<message.length(); i++) 
			if (isHexChar(message.charAt(i)))
				filteredString += message.charAt(i);
		
		return filteredString;
	}
	
	private boolean isHexChar(char c) {
		if (Character.digit(c, 16) == -1)
			return false;
		return true;
	}
}
