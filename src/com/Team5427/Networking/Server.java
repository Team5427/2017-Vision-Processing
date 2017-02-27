package com.Team5427.Networking;

import com.Team5427.VisionProcessing.GraphicsPanel;
import com.Team5427.res.Log;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
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

	private static ArrayList<Interpreter> interpreterList = null;

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
			} catch (Exception e) {
				Log.error(e.getMessage());
			}
		}

		return false;
	}

	@Deprecated
	public static boolean send(String s) {
		if (isConnected()) {
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
	public static boolean isConnected() {
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

						// TODO make sure that these are all working
						if (connection != null && !connection.isClosed() && in != null) {
							try {
								byte bufferSize[] = new byte[Integer.BYTES];
								int dataBufferSize = in.read(bufferSize, 0, bufferSize.length);

								// Ignore any received data when the size of the byte array are less than 1
								if (dataBufferSize < 1) {
									return;
								}

								byte[] dataBuffer = new byte[dataBufferSize];
								int numFromStream = in.read(dataBuffer, 0, dataBuffer.length);
								interpretData(dataBuffer, numFromStream);

//								byte buffer[] = new byte[MAX_BYTE_BUFFER];
//								int bufferWriteIndex = 0;
//								int numFromStream = in.read(buffer, 0, buffer.length);
//
//								if (numFromStream < Integer.BYTES + 1) {
//									throw new Exception("Networking Error: Bytes received from stream is less one plus the size " +
//											"of bytes of int");
//								}
//
//								byte[] buffSizeBytes = new byte[Integer.BYTES];
//								for (int i = 0; i < Integer.BYTES; i++) {
//									buffSizeBytes[i] = buffer[i];
//								}
//
//								int bufferSize = Interpreter.byteArrayToInt(buffSizeBytes);
//								byte[] fullBuffer = new byte[bufferSize];
//
//								Interpreter.addByteArray(fullBuffer, bufferWriteIndex, buffer, Integer.BYTES, numFromStream - Integer.BYTES);
//								bufferWriteIndex += numFromStream - Integer.BYTES;
//								bufferSize -= numFromStream - Integer.SIZE;
//
//								while (bufferSize > 0) {
//									buffer = new byte[MAX_BYTE_BUFFER];
//									numFromStream = in.read(buffer, 0, buffer.length);
//									Interpreter.addByteArray(fullBuffer, bufferWriteIndex, buffer, 0, numFromStream);
//									bufferWriteIndex += numFromStream;
//									bufferSize -= numFromStream;
//								}
//
//								Log.debug("num from stream: " + numFromStream);
//								interpretData(fullBuffer, fullBuffer.length);
//								Log.debug("\n===========================\n");

							} catch (SocketException e) {
//								reconnect();
							} catch (Exception e) {
								Log.error(e.getMessage());
							}

							try {
								Thread.currentThread().sleep(10);
							} catch (InterruptedException e) {
								Log.info("Thread has been interrupted, server thread will attempt to find another client.");
							} catch (Exception e) {
								Log.error(e.getMessage());
							}
						}
					}

				} catch (Exception e) {
					Log.error(e.getMessage());
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
