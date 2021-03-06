package com.Team5427.Networking;

import com.Team5427.VisionProcessing.GraphicsPanel;
import com.Team5427.res.Log;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class Server {

	private static Socket connection = null;
	private static ServerSocket serverSocket;
	private static ObjectInputStream in;
	private static ObjectOutputStream out;
	private static OutputStream byteOutStream;
	private static InputStream byteInputStream;

	private static final int PORT = 25565;

	public static int MAX_BYTE_BUFFER = 256;

	private static ArrayList<Interpreter> interpreterList = new ArrayList<>();

	/**
	 * Sends byte array though the network. The size of the byte array is added at the first four index of a new byte
	 * array
	 * @param buff byte array to send over the network
	 * @return true if sent successfully, false otherwise
	 */
	public static boolean send(byte[] buff) {
		if (isConnected()) {
			try {
				out.write( Interpreter.merge(Interpreter.intToByteArray(buff.length), buff) );
				out.flush();
				return true;
			} catch (SocketException e) {
				e.printStackTrace();
				Log.error("Socket exception acquired");
				reset();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			Log.error("Failed to send byte array " + buff);
		}

		return false;
	}

	/**
	 * Sends a serialized object over the network
	 *
	 * @param obj Serializable object to send
	 * @return true if object is sent successfully
	 */
	public synchronized boolean send(Serializable obj) {
		return send( Interpreter.merge(new byte[]{ ByteDictionary.OBJECT }, Interpreter.serialize(obj)) );
	}

	/**
	 * Sends a message over the network to the client
	 * @param s message to be sent
	 * @return true if message sent successfully, false otherwise
	 */
	public static boolean send(String s) {
		return send( Interpreter.merge(new byte[] { ByteDictionary.MESSAGE }, Interpreter.serialize(s)) );
	}

//	@Deprecated
//	public static boolean send(String s) {
//		if (isConnected()) {
//			try {
//				out.writeObject(s);
//				out.flush();
//				return true;
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//
//		return false;
//	}

	/**
	 * Call whenever sending something to the client in order to check whether
	 * or not it is connected to the server.
	 * 
	 * @return whether the client is connected.
	 */
	public static boolean isConnected() {
		return connection != null && !connection.isClosed();
//				&& serverSocket != null && !serverSocket.isClosed()
//				&& in != null
//				&& out != null;
	}

	public static synchronized void reset() {
		try {
			if (connection != null)
				connection.close();
//			if (serverSocket != null)
//				serverSocket.close();
			if (in != null)
				in.close();
			if (out != null)
				out.close();

			connection = null;
//			serverSocket = null;
//			in = null;
//			out = null;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			// If null, no need to reset
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Starts server thread with default 100ms sleep to give the thread enough time to
	 * initialize
	 */
	public static synchronized void start() {
		start(100);
	}

	/**
	 * Starts server thread
	 */
	public static synchronized void start(long sleep) {

		try {
			serverSocket = new ServerSocket(PORT);
			serverSocket.setSoTimeout(800);

		} catch (Exception e) {
			e.printStackTrace();
		}

		listener.start();

		try {
			Thread.sleep(sleep);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
						Log.pl("Searching for a connection...");
						try {
							Log.debug("Resetting network");
							reset();
							Log.debug("Waiting for a new connection from the client");
							connection = serverSocket.accept();
							out = new ObjectOutputStream(connection.getOutputStream());
							in = new ObjectInputStream(connection.getInputStream());

							if (isConnected())
								Log.debug("~Connection successfully established!");
						} catch (SocketTimeoutException e) {
							Log.debug("No connection found, retrying to reconnect");
						}
						catch (Exception e) {
							Log.error("Attempt to establish a connection failed.");
							e.printStackTrace();
						}
					} else {

						// TODO make sure that these are all working
						if (connection != null && !connection.isClosed() && in != null) {
							try {
								byte bufferSize[] = new byte[Integer.BYTES];
								int dataBufferSize = in.read(bufferSize, 0, bufferSize.length);

								Log.debug("Bytes from network received");

								// Ignore any received data when the size of the byte array are less than 1
								if (dataBufferSize == -1) {
									Log.debug("Connection to client closed");
									reset();
									continue;
								}
								else if (dataBufferSize < 4) {
									System.err.println(Interpreter.toStringByteArray(bufferSize));
									continue;
								}

								int dataSize = Interpreter.byteArrayToInt(bufferSize);
								byte[] dataBuffer = new byte[dataSize];
								int numFromStream = in.read(dataBuffer, 0, dataBuffer.length);
								interpretData(dataBuffer, numFromStream);
							} catch (SocketException e) {
//								reconnect();
							} catch (Exception e) {
								e.printStackTrace();
							}

							try {
								Thread.currentThread().sleep(10);
							} catch (InterruptedException e) {
								Log.debug("Thread has been interrupted, server thread will attempt to find another client.");
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	);

	/**
	 * Interprets byte array received
	 *
	 * @param buff          byte buffer to parse data
	 * @param numFromStream length of usable index from 0
	 */
	public static void interpretData(byte[] buff, int numFromStream) {
		for (Interpreter i : interpreterList) {
			i.interpret(buff, numFromStream);
		}
	}

	public static void addInterpreter(Interpreter... interpreters) {
		if (interpreterList == null) {
			interpreterList = new ArrayList<>(interpreters.length);
		}

		for (Interpreter i : interpreters) {
			interpreterList.add(i);
		}
	}

	public static boolean removeInterpreter(Interpreter interpreter) {
		if (interpreterList == null) {
			return false;
		}

		return interpreterList.remove(interpreter);
	}

	public static Interpreter removeInterpreter(int index) {
		if (interpreterList == null || index > interpreterList.size()) {
			return null;
		}
		return interpreterList.remove(index);
	}
}
