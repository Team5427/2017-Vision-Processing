package com.Team5427.VisionProcessing;

import com.Team5427.Networking.ByteDictionary;
import com.Team5427.Networking.Interpreter;
import com.Team5427.res.Log;

import java.util.ArrayList;


////////////////////////////////////////////////////////////////////////////////////////////////

public class SteamworkInterpreter extends Interpreter {

    public volatile ArrayList<Object> recievedObjects = new ArrayList<>();

    public volatile long lastRecievedTime = 0;

    /**
     * Horizontal angle received from the network
     */
    public volatile double horizontalAngle = 0;

    /**
     * Distance received from the network
     */
    public volatile double distance = 0;

    @Override
    public void interpret(byte[] buff, int numFromStream) {
        Log.debug("buff: " + Interpreter.toStringByteArray(buff));

        if (buff.length < 1) {
            return;
        }

        switch (buff[0]) {
            case ByteDictionary.MESSAGE:
                String message = (String)(deserialize(buff, 1, buff.length - 1));
                Log.pl("Message from RoboRIO: " + message);
                break;
            case ByteDictionary.OBJECT:
                recievedObjects.add( deserialize(buff, 1, buff.length - 1) );
                break;
            case ByteDictionary.TARGET_DATA:
                // Code here
            default:
                Log.debug("Invalid dictionary: " + toStringByteArray(buff));
                break;
        }
    }
}
