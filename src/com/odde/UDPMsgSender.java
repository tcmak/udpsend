package com.odde;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class UDPMsgSender {

	private DatagramSocket socket;

	public UDPMsgSender(DatagramSocket clientSocket) {
		this.socket = clientSocket;
	}

	public void send(DatagramPacket sendPacket) throws IOException {
		socket.send(sendPacket);
	}

	public DatagramPacket createTextMessagePacket(String hostName, int port, String message) throws UnknownHostException {
		return createHexMessagePacket(hostName, port, message.getBytes());
	}
	
	public void close() {
		socket.close();
	}

	public String getResponse(DatagramPacket receivePacket) throws IOException {
		socket.receive(receivePacket);
		return new String(receivePacket.getData());
	}

	public DatagramPacket createHexMessagePacket(String hostName, int port,
			byte[] hexMessage) throws UnknownHostException {
		InetAddress IPAddress = InetAddress.getByName(hostName);
		return new DatagramPacket(hexMessage, hexMessage.length, IPAddress, port);
	}

}
