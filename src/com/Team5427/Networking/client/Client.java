package com.Team5427.Networking.client;

import com.Team5427.Networking.ByteDictionary;
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

    public static final String DEFAULT_IP = "localhost";// "10.54.27.236";
    public static final int DEFAULT_PORT = 25565;

    public static String ip;
    public static int port;
//    public ArrayList<Object> inputStreamData = null;
    Thread networkThread;
    private ArrayList<Interpreter> interpreterList = null;
//	public static GoalData lastRecievedGoal = null;

    private Socket clientSocket;
    private ObjectInputStream is;
    private ObjectOutputStream os;

    public Client() {
        ip = DEFAULT_IP;
        port = DEFAULT_PORT;
    }

    public Client(Interpreter... interpreter) {
        Client.ip = DEFAULT_IP;
        Client.port = DEFAULT_PORT;

        interpreterList = new ArrayList<>(interpreter.length);
        for (int i = 0; i < interpreter.length; i++) {
            interpreterList.add(interpreter[i]);
        }
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

    /**
     * Reconnect to the client to the server
     *
     * @return true if connection is a success, false if failed
     */
    public boolean reconnect() {
        try {
            Log.debug("~Establishing connection...");
            clientSocket = new Socket(ip, port);
            Log.debug("Client Socket Established");
            is = new ObjectInputStream(clientSocket.getInputStream());
            Log.debug("Object Input Stream Established");
            os = new ObjectOutputStream(clientSocket.getOutputStream());
            Log.debug("Object Output Stream Established");
            Log.debug(clientSocket.toString());

//            inputStreamData = new ArrayList<>();

            Log.debug("Connection to the server has been established successfully.");

            return true;
        } catch (Exception e) {
            // TODO removed due to spam
            // System.out.println("Connection failed to establish.");
            Log.debug("Connection failed to establish.");

            try {
                Thread.currentThread().sleep(200);
            } catch (Exception se) {
                se.printStackTrace();
            }

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

//    public ArrayList<Object> getInputStreamData() {
//        return inputStreamData;
//    }

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

    /**
     * Sends a message over the network to the client
     * @param s message to be sent
     * @return true if message sent successfully, false otherwise
     */
    public boolean send(String s) {
        return send( Interpreter.merge(new byte[] { ByteDictionary.MESSAGE }, Interpreter.serialize(s)) );
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
     * Sends byte array though the network. The size of the byte array is added at the first four index of a new byte
     * array
     * @param buff byte array to send over the network
     * @return true if sent successfully, false otherwise
     */
    public boolean send(byte[] buff) {
        if (isConnected()) {
            try {
                os.write( Interpreter.merge(Interpreter.intToByteArray(buff.length), buff) );
                os.flush();
                return true;
            } catch (Exception e) {
                Log.error(e.getMessage());
            }
        }

        return false;
    }

    /**
     * Interprets byte array received
     *
     * @param buff          byte buffer to parse data
     * @param numFromStream length of usable index from 0
     */
    public void interpretData(byte[] buff, int numFromStream) {
        for (Interpreter i : interpreterList) {
            i.interpret(buff, numFromStream);
        }
    }

    /**
     * Add interpreters to the client
     *
     * @param interpreter interpreters to add
     */
    public void addInterpreter(Interpreter... interpreter) {
        for (int i = 0; i < interpreter.length; i++) {
            interpreterList.add(interpreter[i]);
        }
    }

    public void addInterpreter(ArrayList<Interpreter> interpreter) {
        interpreterList.addAll(interpreter);
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
                    byte bufferSize[] = new byte[Integer.BYTES];
                    int dataBufferSize = is.read(bufferSize, 0, bufferSize.length);

                    Log.debug("~Bytes from network received.");

                    if (dataBufferSize == -1) {
                        clientSocket.close();
                        is.close();
                        os.close();
//                        reset();
                        continue;
                    }
                    else if (dataBufferSize < 4) {
                        System.err.println(Interpreter.toStringByteArray(bufferSize));
                        continue;
                    }

                    int dataSize = Interpreter.byteArrayToInt(bufferSize);
                    byte[] dataBuffer = new byte[dataSize];
                    int numFromStream = is.read(dataBuffer, 0, dataBuffer.length);
                    interpretData(dataBuffer, numFromStream);

                    /*      OLD CODE        */
//                    byte buffer[] = new byte[MAX_BYTE_BUFFER];
//                    int bufferWriteIndex = 0;
//                    int numFromStream = is.read(buffer, 0, buffer.length);
//
//                    if (numFromStream < Integer.BYTES + 1) {
//                        throw new Exception("Networking Error: Bytes received from stream is less one plus the size " +
//                                "of bytes of int");
//                    }
//
//                    byte[] buffSizeBytes = new byte[Integer.BYTES];
//                    for (int i = 0; i < Integer.BYTES; i++) {
//                        buffSizeBytes[i] = buffer[i];
//                    }
//
//                    int bufferSize = Interpreter.byteArrayToInt(buffSizeBytes);
//                    byte[] fullBuffer = new byte[bufferSize];
//
//                    Interpreter.addByteArray(fullBuffer, bufferWriteIndex, buffer, Integer.BYTES, numFromStream - Integer.BYTES);
//                    bufferWriteIndex += numFromStream - Integer.BYTES;
//                    bufferSize -= numFromStream - Integer.SIZE;
//
//                    while (bufferSize > 0) {
//                        buffer = new byte[MAX_BYTE_BUFFER];
//                        numFromStream = is.read(buffer, 0, buffer.length);
//                        Interpreter.addByteArray(fullBuffer, bufferWriteIndex, buffer, 0, numFromStream);
//                        bufferWriteIndex += numFromStream;
//                        bufferSize -= numFromStream;
//                    }

//                    Log.debug("num from stream: " + numFromStream);
//                    interpretData(fullBuffer, fullBuffer.length);
//                    Log.debug("\n===========================\n");

                } catch (SocketException e) {
                    Log.error(e.getMessage());
                    reconnect();
                } catch (Exception e) {
                    Log.error(e.getMessage());
                }

                try {
                    networkThread.sleep(10);
                } catch (InterruptedException e) {
                    Log.debug("Thread has been interrupted, client thread will stop.");
                } catch (Exception e) {
                    Log.error(e.getMessage());
                }
            } else {
                Log.debug("Connection lost, attempting to re-establish with driver station.");
                reconnect();

                if (!isConnected()) {
                    try {
                        Thread.currentThread().sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}