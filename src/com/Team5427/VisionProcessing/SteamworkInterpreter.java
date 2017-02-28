package com.Team5427.VisionProcessing;

import com.Team5427.Networking.ByteDictionary;
import com.Team5427.Networking.Interpreter;
import com.Team5427.res.Log;

/**
 * Created by Frian on 2/22/2017.
 */
public class SteamworkInterpreter extends Interpreter {

    @Override
    public void interpret(byte[] buff, int numFromStream) {
        if (buff.length < 1) {
            return;
        }

        switch (buff[0]) {
            case ByteDictionary.MESSAGE:
//                Log.pl("Message from RoboRIO: " + );
                break;
            case ByteDictionary.OBJECT:
                break;
        }
    }
}
