package com.example.demo.client;

import com.example.demo.utils.InputValidator;
import lombok.extern.slf4j.Slf4j;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

@Slf4j
public class StartClient {
    static DatagramSocket socket;
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // 1. Get IP address
        String input;
        do {
            System.out.println("\n===============================");
            System.out.print("Please input the IP address of the server: ");
            input = scanner.next();
        } while (!InputValidator.isValidIp(input));

        int PORT = 2222;

        // 2. Initiate socket connection
        try {
            socket = new DatagramSocket();
            socket.connect(InetAddress.getByName(input), PORT);
        } catch (SocketException | UnknownHostException e) {
            log.error("Socket or UnknownHost Error: " + e.getMessage());
        }

        AppClient client = new AppClient(socket);

        while (true) {
            client.run();
        }
    }
}
