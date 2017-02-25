package com.Team5427.VisionProcessing;

import com.Team5427.res.Config;
import com.Team5427.Networking.Server;

import java.util.*;

public class ByteSender implements Runnable{

    private Scanner scanner = new Scanner(System.in);

    @Override
    public void run() {


        while (Config.BYTE_SENDER_THREAD_ENABLE) {
            if (Server.isConnected()) {
                System.out.println("~Selection~ (Enter ints only)");
                System.out.println("1. Enter byte array");
                System.out.println("2. Read Data");
                System.out.println("0. Exit");
                System.out.print("Enter selection: ");
                int selection = scanner.nextInt();

                if (selection == 0) {

                    System.out.println("Terminating...");
                    break;
                }
                else if (selection == 1) {

                    System.out.print("Enter size of byte array: ");
                    int sizeArr = scanner.nextInt();
                    byte[] arr = new byte[sizeArr];

                    for (int i = 0; i < arr.length; i++) {

                        System.out.print("Enter byte for index " + i + ": ");
                        arr[i] = scanner.nextByte();
                    }

                    Server.send(arr);
                }
                else if (selection == 2) {

                    System.out.println("TODO: Code this");
                }

            }
            else {

                System.out.println("Waiting connection from client...");

                try {

                    Thread.currentThread().sleep(500);
                } catch (InterruptedException e) {

                    System.err.println("Failed to sleep in ByteSender thread.");
                }
            }
        }
    }
}
