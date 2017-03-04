package com.Team5427.VisionProcessing;

import com.Team5427.Networking.ByteDictionary;
import com.Team5427.Networking.Interpreter;
import com.Team5427.res.Log;

import java.util.ArrayList;

/**
 * Created by Charlemagne Wong on 2/22/2017.
 */
public class SteamworkInterpreter extends Interpreter {

    public volatile ArrayList<Object> recievedObjects = new ArrayList<>();

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
            default:
                Log.debug("Invalid dictionary: " + toStringByteArray(buff));
                break;
        }
    }
}
