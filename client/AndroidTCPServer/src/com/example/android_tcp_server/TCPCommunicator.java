package com.example.android_tcp_server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

public class TCPCommunicator {
	private static TCPCommunicator uniqInstance;
	private static int serverPort;
	private static List<OnTCPMessageRecievedListener> allListeners;
	private static ServerSocket ss;
	private static Socket s;
	private static BufferedReader in;
	private static BufferedWriter out;
	private static OutputStream outputStream;
	private static Handler handler = new Handler();
	private TCPCommunicator()
	{
		allListeners = new ArrayList<OnTCPMessageRecievedListener>();
	}
	public static TCPCommunicator getInstance()
	{
		if(uniqInstance==null)
		{
			uniqInstance = new TCPCommunicator();
		}
		return uniqInstance;
	}
	public  TCPWriterErrors init(int port)
	{
		setServerPort(port);
		InitTCPServerTask task = new InitTCPServerTask();
		task.execute(new Void[0]);
		return TCPWriterErrors.OK;
//		}
	}
	public static  TCPWriterErrors writeToSocket(JSONObject obj)
	{
		try
		{
		out.write(obj.toString() + System.getProperty("line.separator"));
		out.flush();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return TCPWriterErrors.OK;
		
	}
	
	public static void addListener(OnTCPMessageRecievedListener listener)
	{
		allListeners.add(listener);
	}
	


	public static int getServerPort() {
		return serverPort;
	}
	public static void setServerPort(int serverPort) {
		TCPCommunicator.serverPort = serverPort;
	}


	public class InitTCPServerTask extends AsyncTask<Void, Void, Void>
	{
		public InitTCPServerTask()
		{
			
		}

		@Override
		protected Void doInBackground(Void... params) {

			
			try {
				ss = new ServerSocket(TCPCommunicator.getServerPort());
				
				s = ss.accept();
	             in = new BufferedReader(new InputStreamReader(s.getInputStream()));
	             outputStream = s.getOutputStream();
	             out = new BufferedWriter(new OutputStreamWriter(outputStream));
	            //receive a message
	             String incomingMsg;
	            while((incomingMsg=in.readLine())!=null)
	            {
	            	final String finalMessage=incomingMsg;
	            	handler.post(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							 for(OnTCPMessageRecievedListener listener:allListeners)
					            	listener.onTCPMessageRecieved(finalMessage);
					            Log.e("TCP", finalMessage);
						}
					});     
	            }
			} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			}
			return null;
			
			
			
		}
		
	}
	
	public enum TCPWriterErrors{UnknownHostException,IOException,otherProblem,OK}

	public static void closeStreams() {
		// TODO Auto-generated method stub
		try
		{
			s.close();
			ss.close();
			out.close();
			in.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
