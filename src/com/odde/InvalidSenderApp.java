package com.odde;

import java.io.PrintStream;

public class InvalidSenderApp extends UDPMsgSenderApp {

	private PrintStream out;

	public InvalidSenderApp(PrintStream out) {
		this.out = out;
	}

	@Override
	public void execute(String hostName, int port, String message) {
		out.println(UDPMsgSenderApp.USAGE_DESCRIPTION);
	}
}
