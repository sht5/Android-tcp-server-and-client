package com.example.android_tcp_client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public class TCPCommunicator {
	private static TCPCommunicator uniqInstance;
	private static String serverHost;
	private static int serverPort;
	private static List<TCPListener> allListeners;
	private static BufferedWriter out;
	private static BufferedReader in;
	private static Socket s;
	private static Handler UIHandler;
	private static Context appContext;
	private TCPCommunicator()
	{
		allListeners = new ArrayList<TCPListener>();
	}
	public static TCPCommunicator getInstance()
	{
		if(uniqInstance==null)
		{
			uniqInstance = new TCPCommunicator();
		}
		return uniqInstance;
	}
	public  TCPWriterErrors init(String host,int port)
	{
		setServerHost(host);
		setServerPort(port);
		InitTCPClientTask task = new InitTCPClientTask();
		task.execute(new Void[0]);
		return TCPWriterErrors.OK;
	}
	public static  TCPWriterErrors writeToSocket(final JSONObject obj,Handler handle,Context context)
	{
		UIHandler=handle;
		appContext=context;
		Runnable runnable = new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try
				{
			        String outMsg = obj.toString() + System.getProperty("line.separator"); 
			        out.write(outMsg);
			        out.flush(); 
			        Log.i("TcpClient", "sent: " + outMsg);
				}
				catch(Exception e)
				{
					UIHandler.post(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							Toast.makeText(appContext ,"a problem has occured, the app might not be able to reach the server", Toast.LENGTH_SHORT).show();
						}
					});
				}
			}
			
		};
		Thread thread = new Thread(runnable);
		thread.start();
		return TCPWriterErrors.OK;
		
	}
	
	public static void addListener(TCPListener listener)
	{
		allListeners.clear();
		allListeners.add(listener);
	}
	public static void removeAllListeners()
	{
		allListeners.clear();
	}
	public static void closeStreams()
	{
		try
		{
			s.close();
			in.close();
			out.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	

	public static String getServerHost() {
		return serverHost;
	}
	public static void setServerHost(String serverHost) {
		TCPCommunicator.serverHost = serverHost;
	}
	public static int getServerPort() {
		return serverPort;
	}
	public static void setServerPort(int serverPort) {
		TCPCommunicator.serverPort = serverPort;
	}


	public class InitTCPClientTask extends AsyncTask<Void, Void, Void>
	{
		public InitTCPClientTask()
		{
			
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub

			try
			{
				s = new Socket(getServerHost(), getServerPort());
		         in = new BufferedReader(new InputStreamReader(s.getInputStream()));
		         out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
		         for(TCPListener listener:allListeners)
			        	listener.onTCPConnectionStatusChanged(true);
		        while(true)
		        {
		        	String inMsg = in.readLine();
		        	if(inMsg!=null)
		        	{
				        Log.i("TcpClient", "received: " + inMsg);
				        for(TCPListener listener:allListeners)
				        	listener.onTCPMessageRecieved(inMsg);
		        	}
		        }

		    } catch (UnknownHostException e) {
		        e.printStackTrace();
		    } catch (IOException e) {
		        e.printStackTrace();
		    } 
			
			return null;
			
		}
		
	}
	public enum TCPWriterErrors{UnknownHostException,IOException,otherProblem,OK}
}
