package com.Team5427.Networking.client;

import com.Team5427.Networking.ByteDictionary;
import com.Team5427.Networking.GoalData;
import com.Team5427.Networking.Interpreter;
import com.Team5427.res.Log;

/**
 * READ ME: Everything below the comment line should be copied and pasted to the client
 * 			on the main repository. Anything above here is to allow compatibility without
 * 			changing other parts of the code for the robot.
 */

///////////////////////////////////////////////////////////////////////////////////

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class Client implements Runnable {

	public static final String DEFAULT_IP = "127.0.0.1";// "10.54.27.236";
	public static final int DEFAULT_PORT = 25565;
	public static int MAX_BYTE_BUFFER = 256;

	public static String ip;
	public static int port;

	Thread networkThread;
	public ArrayList<Object> inputStreamData = null;

	private ArrayList<Interpreter> interpreterList = null;
//	public static GoalData lastRecievedGoal = null;

	private Socket clientSocket;
	private ObjectInputStream is;
	private ObjectOutputStream os;

	public Client() {
		ip = DEFAULT_IP;
		port = DEFAULT_PORT;
	}

	public Client(String ip, int port, Interpreter... interpreter) {
		Client.ip = ip;
		Client.port = port;

		interpreterList = new ArrayList<Interpreter>(interpreter.length);
		for (int i = 0; i < interpreterList.size(); i++) {
		    interpreterList.add(interpreter[i]);
        }
	}

	public Client(String ip, int port, ArrayList<Interpreter> interpreterList) {
        Client.ip = ip;
        Client.port = port;
        this.interpreterList = interpreterList;
    }

	/**
	 * Reconnect to the client to the server
	 *
	 * @return true if connection is a success, false if failed
	 */
	public boolean reconnect() {
		try {
			clientSocket = new Socket(ip, port);
			is = new ObjectInputStream(clientSocket.getInputStream());
			os = new ObjectOutputStream(clientSocket.getOutputStream());
			Log.debug(clientSocket.toString());

			inputStreamData = new ArrayList<>();

			Log.info("Connection to the server has been established successfully.");

			return true;
		} catch (Exception e) {
			// TODO removed due to spam
			// System.out.println("Connection failed to establish.");
			Log.info("Connection failed to establish.");
			return false;
		}
	}

	/**
	 * Checks if the client is connected to a server
	 *
	 * @return true if server connection is established, false if not.
	 */
	public boolean isConnected() {
		return clientSocket != null && !clientSocket.isClosed();
	}

	public static String getIp() {
		return ip;
	}

	public static void setIp(String ip) {
		Client.ip = ip;
	}

	public static int getPort() {
		return port;
	}

	public static void setPort(int port) {
		Client.port = port;
	}

	public ArrayList<Object> getInputStreamData() {
		return inputStreamData;
	}

	/**
	 * Sends an object to the server
	 *
	 * @param t
	 *            object to be sent to the server
	 * @return true if the object is sent successfully, false if otherwise.
	 */
	/*
	 * public synchronized boolean send(Task t) {
	 * 
	 * if (networkThread != null && !networkThread.isInterrupted()) { try {
	 * os.writeObject(t); os.reset(); return true; } catch
	 * (NotSerializableException e) { Log.error(getClass() +
	 * ":: send(Serializable o)\n\tThe object to be sent is not serializable.");
	 * } catch (SocketException e) { Log.error("Socket Exception"); } catch
	 * (NullPointerException e) { Log.error(
	 * "\n\tThere was an error connecting to the server."); // This error occurs
	 * when the client attempts to connect to a server, but the running } catch
	 * (Exception e) { Log.error(e.getMessage()); } }
	 * 
	 * return false; }
	 */

	/**
	 * Enables the thread to start receiving data from a network
	 *
	 * @return true if the thread starts successfully, false if otherwise.
	 */
	public synchronized boolean start() {
		if (networkThread == null && (clientSocket == null || !clientSocket.isClosed())) {
			networkThread = new Thread(this);
			networkThread.start();
			return true;
		}

		return false;
	}

	/**
	 * Stops the thread from receiving data from the server
	 *
	 * @return true if the thread is stopped successfully, false if otherwise.
	 */
	public synchronized boolean stop() {
		networkThread.interrupt();

		try {
			clientSocket.close();
			os.close();
			is.close();
		} catch (Exception e) {
			Log.error(e.getMessage());
		}

		clientSocket = null;
		os = null;
		is = null;

		if (!networkThread.isAlive()) { // The thread is found running and is
										// told to stop
			return true;
		} else { // The thread is not running in the first place
			return false;
		}
	}

	public void interpretData(byte[] buff, int numFromStream) {

	    for (Interpreter i : interpreterList) {
	        i.interpret(buff, numFromStream);
        }

//		switch (buff[0]) {
//		case ByteDictionary.GOAL_ATTACHED:
//
//			lastRecievedGoal = new GoalData(buff);
//			Log.debug("Data from goal: Motor Value-" + lastRecievedGoal.getMotorValue() + " X Angle-"
//					+ Math.toDegrees(lastRecievedGoal.getHorizontalAngle()));
//			Log.debug("Data from received bytes: " + getStringByteBuffer(buff));
//
//			break;
//
//		case ByteDictionary.LOG:
//
//			Log.vision(new String(getBufferedSegment(buff, 1, numFromStream - 1)));
//
//			break;
//
//		}

	}

	/**
	 * Running method that receives data from the server.
	 */
	@Override
	public void run() {

		reconnect();

		while (!networkThread.isInterrupted()) {

			if (clientSocket != null && !clientSocket.isClosed() && is != null) {
				try {
					byte buffer[] = new byte[MAX_BYTE_BUFFER];
					int numFromStream = is.read(buffer, 0, buffer.length);

					Log.debug("num from stream: " + numFromStream);
					interpretData(buffer, numFromStream);
//					System.out.println(lastRecievedGoal.toString());
					Log.debug("\n===========================\n");

				} catch (SocketException e) {
					reconnect();
				} catch (Exception e) {
					Log.error(e.getMessage());
				}

				try {
					networkThread.sleep(10);
				} catch (InterruptedException e) {
					Log.info("Thread has been interrupted, client thread will stop.");
				} catch (Exception e) {
					Log.error(e.getMessage());
				}
			} else {
				Log.info("Connection lost, attempting to re-establish with driver station.");
				reconnect();
			}
		}
	}

	public static String getStringByteBuffer(byte[] buff) {
		String str = "[";

		for (int i = 0; i < buff.length; i++)
			str += buff[i] + ",";

		return str + "]";
	}

	public static byte[] getBufferedSegment(byte[] buff, int startPos, int length) {
		byte[] temp = new byte[length];

		for (int i = 0; i < length; i++) {
			System.out.println("buffereing");
			temp[i] = buff[startPos + i];
		}

		return temp;
	}

	public static byte[] longToBytes(long l) {
		byte[] val = new byte[8];
		for (int i = 7; i >= 0; i--) {
			val[i] = (byte)(l & 0xFF);
			l >>= 8;
		}
		return val;
	}

	public static long bytesToLong(byte[] b) {
		long val = 0;
		for (int i = 0; i < 8; i++) {
			val <<= 8;
			val |= (b[i] & 0xFF);
		}
		return val;
	}
}