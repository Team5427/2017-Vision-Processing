package com.Team5427.Networking;

import com.Team5427.VisionProcessing.GraphicsPanel;
import com.Team5427.res.Log;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;

public class Server {

	private static Socket connection = null;
	private static ServerSocket serverSocket;
	private static ObjectInputStream in;
	private static ObjectOutputStream out;
	private static OutputStream byteOutStream;
	private static InputStream byteInputStream;

	private static final int PORT = 25565;

	/**
	 * TODO finish this
	 * Sends a byte array over the network
	 * @param buff byte array to send over the network
	 * @return true if sent successfully, false otherwise
	 */
	public static boolean send(byte[] buff) {
		if (hasConnection()) {
			try {
				int size = buff.length;
				byte[] toSend = new byte[Long.BYTES + size];
				byte[] sizeByte = ByteBuffer.allocate(4).putInt(size).array();

				for (int i = 0; i < Integer.BYTES; i++)

				out.write(buff);
				out.flush();
				return true;
			} catch (Exception e) {
				Log.error(e.getMessage());
			}
		}

		return false;
	}

	@Deprecated
	public static boolean send(String s) {
		if (hasConnection()) {
			try {
				out.writeObject(s);
				out.flush();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return false;
	}

	/**
	 * Call whenever sending something to the client in order to check whether
	 * or not it is connected to the server.
	 * 
	 * @return whether the client is connected.
	 */
	public static boolean hasConnection() {
		return (connection != null && !connection.isClosed());
	}

	public static synchronized void reset() {
		try {
			connection.close();
			// serverSocket.close();
			in.close();
			out.close();
			connection = null;
			// serverSocket = null;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Starts server thread
	 */
	public static synchronized void start() {

		try {
			serverSocket = new ServerSocket(PORT);
			serverSocket.setSoTimeout(800);

		} catch (Exception e) {
			e.printStackTrace();
		}

		listener.start();
	}

	/**
	 * Stops server thread
	 * WARNING: New server class might be needed if thread is stopped
	 */
	public static synchronized void stop() {
		listener.interrupt();

		try {
			connection.close();
			serverSocket.close();
			in.close();
			out.close();
			connection = null;
			serverSocket = null;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Thread listener = new Thread(new Runnable() {

		@Override
		public void run() {

			while (!listener.isInterrupted()) {
				try {

					if (connection == null || connection.isClosed()) {
						Log.p("Searching for a connection...");
						try {

							connection = serverSocket.accept();
							out = new ObjectOutputStream(connection.getOutputStream());
							in = new ObjectInputStream(connection.getInputStream());

							if (connection != null && !connection.isClosed())
								Log.p("Connected!");
						} catch (Exception e) {
						}
					} else {
						String s = in.readUTF();

						// TODO make sure that these are all working

						if (s.contains(StringDictionary.TASK)) {

							s = s.substring(StringDictionary.TASK.length(), s.length() - 1);

							if (s.contains(StringDictionary.GOAL_ATTACHED)) {

							} else if (s.contains(StringDictionary.LOG)) {

								send(StringDictionary.TASK + StringDictionary.LOG
										+ "roborio told the driverstation to log something, it should be the other way around.");

							} else if (s.contains(StringDictionary.MESSAGE)) {

								System.out.println("ROBORIO replied with message: " + s);

							} else if (s.contains(StringDictionary.TELEOP_START)) {

								GraphicsPanel.taskCommand(s);

							} else if (s.contains(StringDictionary.AUTO_START)) {

								GraphicsPanel.taskCommand(s);

							} else {
								System.out.println("Valid task was recieved, but with unrecognized contents.");
							}

						} else {
							System.out.println("unrecognized task");
						}

					}

				} catch (SocketException e) {
					System.out.println(
							"\n\tConnection to the client has been lost. Attempting to re-establish connection");
					reset();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	);

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
