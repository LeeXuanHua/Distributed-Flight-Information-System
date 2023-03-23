package com.example.demo.client;

import com.example.demo.server.servant.models.ClientRequest;
import com.example.demo.server.servant.models.Monitoring;
import com.example.demo.utils.InputValidator;
import com.example.demo.utils.MarshallUtil;
import com.example.demo.utils.ReqOrReplyEnum;
import com.example.demo.utils.Simulate;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Scanner;

@Slf4j
public class AppClient {
    DatagramSocket socket;

    int MESSAGE_ID;

    public AppClient(DatagramSocket socket) {
        this.socket = socket;
        this.MESSAGE_ID = 0;
    }

    public void run() {
        try {
            // 1. Get user choice
            Scanner scanner = new Scanner(System.in);
            String choice;
            do {
                System.out.println("\n===============================");
                System.out.println("Service 1: Get Flights by Source & Destination");
                System.out.println("Service 2: Get Flights by ID");
                System.out.println("Service 3: Book seats on a flight");
                System.out.println("Service 4: Monitor flight availability");
                System.out.println("Service 5: Delete flight booking");
                System.out.println("Service 6: Update flight booking");
                System.out.print("Make your selection (1-6): ");
                choice = scanner.next();
            } while (!InputValidator.isInteger(choice, Optional.of(1), Optional.of(6)));

            // 2. Construct and marshall request
            ClientRequest clientRequest = ClientServices.getService(++MESSAGE_ID, choice, scanner);
            byte[] marshalledRequest = MarshallUtil.marshall(clientRequest);
            DatagramPacket requestPacket = new DatagramPacket(marshalledRequest, marshalledRequest.length);

            Object responseObject = handleRequest(requestPacket);
            System.out.println("Response: " + responseObject);
            
            // Handle flight monitoring
            if (Integer.parseInt(choice) == 4) {
                Monitoring monitor = (Monitoring) responseObject;
                LocalDateTime expiry = monitor.getExpiry();
                handleCallback(expiry);
            }

        // anything requiring a socket function will need to have this catch block
        } catch (IOException e) {
            log.error("IOError: " + e.getMessage());
        }
    }

    private void handleCallback(LocalDateTime expiryTime) throws IOException {
        while (LocalDateTime.now().isBefore(expiryTime)) {
            byte[] replyBuffer = new byte[1024];
            DatagramPacket replyPacket = new DatagramPacket(replyBuffer, replyBuffer.length);
            socket.receive(replyPacket);

            if (LocalDateTime.now().isAfter(expiryTime)) break;

            byte[] marshalledReply = replyPacket.getData();
            Object updatedFlight = MarshallUtil.unmarshall(marshalledReply);
            System.out.println("The seat availability of the flight has changed to " + updatedFlight);
        }
    }

    private Object receiveReply(DatagramPacket requestPacket) throws IOException {
        byte[] replyBuffer = new byte[1024];
        DatagramPacket replyPacket = new DatagramPacket(replyBuffer, replyBuffer.length);
        boolean receivedReply = false;

        // Listen for reply
        // If did not simulate a request message loss, then send request
        while (!receivedReply) {
            try {
                socket.receive(replyPacket);
                log.info("Reply received: " + new String(replyPacket.getData(), StandardCharsets.UTF_8).trim());
                receivedReply = true;
            } catch (SocketTimeoutException e) {
                sendRequest(socket, requestPacket);
            }
        }

        byte[] marshalledReply = replyPacket.getData();
        Object unmarshalledReply = MarshallUtil.unmarshall(marshalledReply);
        return unmarshalledReply;
    }

    private Object handleRequest(DatagramPacket requestPacket) throws SocketException, IOException {
        socket.setSoTimeout(5000);
        sendRequest(socket, requestPacket);
        return receiveReply(requestPacket);
    }

    private void sendRequest(DatagramSocket socket, DatagramPacket requestPacket) throws IOException {
        if (!(Simulate.isFailure(ReqOrReplyEnum.REQUEST))) {
            socket.send(requestPacket);
        }
    }
}
