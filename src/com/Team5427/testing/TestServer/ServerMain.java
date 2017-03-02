package com.Team5427.testing.TestServer;

import com.Team5427.Networking.ByteDictionary;
import com.Team5427.Networking.Interpreter;
import com.Team5427.Networking.Server;

import java.util.Scanner;

/**
 * Created by Frian on 2/28/2017.
 */
public class ServerMain {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Server.start();

        do {
            if (!Server.isConnected()) {
                System.out.println("~No connection, waiting until connected");
                // Do nothing, automatically tries to find a new connection
            } else {
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
