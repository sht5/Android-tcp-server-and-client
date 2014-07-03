package com.example.android_tcp_client;

public interface TCPListener {
	public void onTCPMessageRecieved(String message);
	public void onTCPConnectionStatusChanged(boolean isConnectedNow);
}
