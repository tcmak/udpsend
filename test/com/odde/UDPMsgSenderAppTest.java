package com.odde;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.UnknownHostException;

public class UDPMsgSenderAppTest {

	private static final String DASH_H = "-h";
	private static final int PORT_NUMBER = 6800;
	private static final String HOST_NAME = "localhost";
	private static final String textMessage = "Hello";
	PrintStream mockOutputStream = null;
	DatagramSocket mockSocket = null;
	private static final String DASH_T = "-t";
	
	@Before
	public void setUp() {
		mockOutputStream = mock(PrintStream.class);
		mockSocket = mock(DatagramSocket.class);
	}
	
	@Test
	public void UDPMsgSenderAppFactoryCreatesInvalidAppAsDefault() {
		UDPMsgSenderApp senderApp = UDPMsgSenderAppFactory.createApp("invalidType", null, null);
		assertTrue(senderApp instanceof InvalidSenderApp);
	}

	@Test
	public void InvalidSenderShouldDisplayUsageInformation() throws UnknownHostException, IOException {
		UDPMsgSenderApp senderApp = UDPMsgSenderAppFactory.createApp("invalidType", mockOutputStream, null);
		senderApp.execute(null, 0, null);
		verify(mockOutputStream).println(UDPMsgSenderApp.USAGE_DESCRIPTION);
	}
	
	@Test
	public void UDPMsgSenderAppFactoryCreatesTextAppMessageSenderOnDashT() {
		UDPMsgSenderApp senderApp = UDPMsgSenderAppFactory.createApp(DASH_T, null, null);
		assertTrue(senderApp instanceof UDPTextMsgSenderApp);
	}

	@Test
	public void UDPMsgSenderAppFactoryShouldSendAndReceiveTextMessages() throws IOException {
		UDPMsgSenderApp senderApp = UDPMsgSenderAppFactory.createApp(DASH_T, mockOutputStream, mockSocket);
		
		DatagramPacket sendPacket = (new UDPMsgSender(mockSocket)).createTextMessagePacket(HOST_NAME, PORT_NUMBER, textMessage);
		DatagramPacket receivePacket = new DatagramPacket(textMessage.getBytes(), textMessage.getBytes().length);
		
		doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				((DatagramPacket)args[0]).setData(textMessage.getBytes());
				return null;
			}
		}).when(mockSocket).receive((DatagramPacket) anyObject());
		
		senderApp.execute(HOST_NAME, PORT_NUMBER, textMessage);
		
		verify(mockSocket).send(argThat(new SendPacketComparator(sendPacket)));
		verify(mockSocket).receive(argThat(new ReceivePacketComparator(receivePacket)));
		verify(mockOutputStream).println(textMessage);
		verify(mockSocket).close();
	}

	@Test
	public void UDPMsgSenderAppFactoryCreatesHexAppMessageSenderOnDashT() {
		UDPMsgSenderApp senderApp = UDPMsgSenderAppFactory.createApp(DASH_H, null, null);
		assertTrue(senderApp instanceof UDPHexMsgSenderApp);
	}

	@Test
	public void UDPMsgSenderAppFactoryShouldSendAndReceiveHexMessages() throws IOException {
		final byte[] sendMessageInHex = {0,1,2,3,4,5,6,7};
		final String sendMessageInString = "00,01,02,03,04,05,06,07";
		
		UDPMsgSenderApp senderApp = UDPMsgSenderAppFactory.createApp(DASH_H, mockOutputStream, mockSocket);
		
		DatagramPacket sendPacket = (new UDPMsgSender(mockSocket)).createHexMessagePacket(HOST_NAME, PORT_NUMBER, sendMessageInHex);
		DatagramPacket receivePacket = new DatagramPacket(textMessage.getBytes(), textMessage.getBytes().length);
		
		doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				((DatagramPacket)args[0]).setData(textMessage.getBytes());
				return null;
			}
		}).when(mockSocket).receive((DatagramPacket) anyObject());
		
		senderApp.execute(HOST_NAME, PORT_NUMBER, sendMessageInString);
		
		verify(mockSocket).send(argThat(new SendPacketComparator(sendPacket)));
		verify(mockSocket).receive(argThat(new ReceivePacketComparator(receivePacket)));
		verify(mockOutputStream).println(textMessage);
		verify(mockSocket).close();
	}
	
	@Test
	public void UDPMsgSenderAppFactoryShouldReportInvalidHexString() throws IOException {
		final String sendMessageInString = "00,01,02,03,04,05,06,0";
		
		UDPMsgSenderApp senderApp = UDPMsgSenderAppFactory.createApp(DASH_H, mockOutputStream, mockSocket);
		
		senderApp.execute(HOST_NAME, PORT_NUMBER, sendMessageInString);
		verify(mockOutputStream).println(UDPHexMsgSenderApp.INVALID_HEX_STRING);
	}
	
	
	abstract class PacketComparator extends ArgumentMatcher<DatagramPacket> {
		DatagramPacket packet;
		
		public boolean matches(Object expectedPacket) {
			assertEquals(packet.getPort(), ((DatagramPacket) expectedPacket).getPort());
			assertArrayEquals(packet.getData(), ((DatagramPacket) expectedPacket).getData());
			return true;
		}
	}
	
	class SendPacketComparator extends PacketComparator {
		
		public SendPacketComparator(DatagramPacket sendPacket) {
			this.packet = sendPacket;
		}

		public boolean matches(Object expectedPacket) {
			if (super.matches(expectedPacket) == false) return false;
			assertEquals(packet.getAddress().getHostName(), ((DatagramPacket) expectedPacket).getAddress().getHostName());
			assertEquals(packet.getSocketAddress().toString(), ((DatagramPacket) expectedPacket).getSocketAddress().toString());
			return true;
		}
	}
	
	class ReceivePacketComparator extends PacketComparator {
		
		public ReceivePacketComparator(DatagramPacket sendPacket) {
			this.packet = sendPacket;
		}
		
		public boolean matches(Object expectedPacket) {
			if (super.matches(expectedPacket) == false) return false;
			assertEquals((new String(packet.getData())).trim(), (new String(((DatagramPacket) expectedPacket).getData())).trim());
			return true;
		}
	}	
}
