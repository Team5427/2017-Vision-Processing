package com.Team5427.testing.TestClient;

import com.Team5427.Networking.client.Client;
import com.Team5427.VisionProcessing.SteamworkInterpreter;

import java.util.Scanner;

/**
 * Created by Frian on 2/28/2017.
 */
public class ClientMain {

    public static void main(String[] args) {
        Client client = new Client(new SteamworkInterpreter());
        client.start();

        Scanner scanner = new Scanner(System.in);

        do {


            if (!client.isConnected()) {
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
                        client.send(str);
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
