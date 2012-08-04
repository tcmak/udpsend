package com.odde;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class UDPMsgSenderTest {

	private DatagramSocket clientSocket;
	private DatagramSocket mockSocket;
	private UDPMsgSender sender;
	private String textMessage;
	private String hostName;
	private int port;
	private byte[] hexMessage;

	@Before
	public void setUp() throws SocketException {
		clientSocket = new DatagramSocket();
		mockSocket = mock(DatagramSocket.class);
		sender = new UDPMsgSender(mockSocket);
		textMessage = "Hello World";
		hexMessage = new byte[] {1,2,3,4,5,6,7,8,0x0A};
		hostName = "localhost";
		port = 6800;
	}
	
	@After
	public void tearDown() {
		sender.close();
		clientSocket.close();
		verify(mockSocket).close();
	}
	
	@Test
	public void createTextMessagePacket() throws UnknownHostException {
		DatagramPacket sendPacket = sender.createTextMessagePacket(hostName, port, textMessage);

		assertEquals(new String(sendPacket.getData()), textMessage);
		assertEquals(sendPacket.getAddress().getHostName(), hostName);
		assertEquals(sendPacket.getPort(), port);
	}
	
	@Test
	public void createHexMessagePacket() throws UnknownHostException {
		DatagramPacket sendPacket = sender.createHexMessagePacket(hostName, port, hexMessage);

		assertEquals(sendPacket.getData(), hexMessage);
		assertEquals(sendPacket.getAddress().getHostName(), hostName);
		assertEquals(sendPacket.getPort(), port);
	}
	
	@Test
	public void sendTextMessage() throws IOException {
		DatagramPacket sendPacket = sender.createTextMessagePacket(hostName, port, textMessage);

		sender.send(sendPacket);
		verify(mockSocket).send(sendPacket);
	}

	@Test
	public void sendHexMessage() throws IOException {
		DatagramPacket sendPacket = sender.createHexMessagePacket(hostName, port, hexMessage);
		
		sender.send(sendPacket);
		verify(mockSocket).send(sendPacket);
	}
	
	@Test
	public void receiveTextMessage() throws IOException {
		byte[] receiveData = new byte[1024];
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		
		doAnswer(new Answer<DatagramPacket>() {
			public DatagramPacket answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				((DatagramPacket)args[0]).setData(textMessage.getBytes());
				return null;
			}
		}).when(mockSocket).receive(receivePacket);
		
		String reply = sender.getResponse(receivePacket);
		verify(mockSocket).receive(receivePacket);
		
		assertEquals(textMessage, reply);
	}
}
