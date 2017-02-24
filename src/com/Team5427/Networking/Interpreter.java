package com.Team5427.Networking;

/**
 * Created by Charlemagne Wong on 2/21/2017.
 */

/**
 * Interprets byte arrays received over the network
 */
public interface Interpreter {

    void interpret(byte[] buff, int numFromStream);
}
