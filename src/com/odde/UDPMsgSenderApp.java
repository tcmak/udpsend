package com.odde;

import java.io.IOException;

abstract class UDPMsgSenderApp {

	static public final String USAGE_DESCRIPTION = "Usage: <Hostname> <Port> <Message Type> <Message>";
	abstract public void execute(String hostName, int port, String message) throws IOException;

}
