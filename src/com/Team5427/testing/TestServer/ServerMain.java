package com.Team5427.testing.TestServer;

import com.Team5427.Networking.Server;
import com.Team5427.VisionProcessing.SteamworkInterpreter;

import java.util.Scanner;

/**
 * Created by Charlemagne Wong on 2/28/2017.
 */
public class ServerMain {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Server.addInterpreter(new SteamworkInterpreter());
        Server.start();

        boolean connectedTrigger = false;

        do {
            try {
                Thread.sleep(100);
            } catch (Exception e) {

            }

            if (!Server.isConnected()) {
                if (!connectedTrigger) {
                    connectedTrigger = true;
                    System.out.println("~No connection, waiting until connected");
                }
            } else {
                if (connectedTrigger) {
                    connectedTrigger = false;
                    System.out.println("Connected!");
                }
                try {
                    System.out.println("Enter byte to send over the network");
                    System.out.println("1. Message");
                    System.out.print("Enter your selection: ");
                    // Selection
                    int selection = scanner.nextInt();
                    byte dataSelection = -1;

                    if (selection == 1) {
                        System.out.print("Enter the message you want to send: ");
                        String str = scanner.nextLine();
                        str = scanner.nextLine();
                        Server.send(str);
                    } else {
                        System.out.println("Invalid selection.\n");
                        continue;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } while (true);
    }
}
