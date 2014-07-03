package com.example.android_tcp_server;

import java.util.Arrays;
import java.util.List;

import com.example.android_tcp_server.EnumsAndStatics.MessageTypes;

public class EnumsAndStatics {
public enum MessageTypes{MessageFromServer,MessageFromClient,REGISTRATION_APPROVED}
public static final String MESSAGE_TYPE_FOR_JSON ="messageType";
public static final String MESSAGE_CONTENT_FOR_JSON ="messageContent";

public static final String SERVER_IP_PREF ="pref_ip";
public static final String SERVER_PORT_PREF ="pref_port";

public static MessageTypes getMessageTypeByString(String messageInString)
{
	if(messageInString.equals(MessageTypes.MessageFromServer.toString()))
		return MessageTypes.MessageFromServer;
	if(messageInString.equals(MessageTypes.MessageFromClient.toString()))
		return MessageTypes.MessageFromClient;
	if(messageInString.equals(MessageTypes.REGISTRATION_APPROVED.toString()))
		return MessageTypes.REGISTRATION_APPROVED;
	return null;
}
}