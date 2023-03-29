package com.example.demo.client;

import com.example.demo.server.ServerReply;
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
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
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

    public boolean run() {
        try {
            // 1. Get user choice
            Scanner scanner = new Scanner(System.in);
            String choice;
            do {
                System.out.println("\n===============================");
                System.out.println("1: Get Flights by Source & Destination");
                System.out.println("2: Get Flights by ID");
                System.out.println("3: Book seats on a flight");
                System.out.println("4: Monitor flight availability");
                System.out.println("5: Delete flight booking");
                System.out.println("6: Update flight booking");
                System.out.println("7: Terminate client");
                System.out.print("Make your selection (1-7): ");
                choice = scanner.next();
            } while (!InputValidator.isInteger(choice, Optional.of(1), Optional.of(7)));

            if (Integer.parseInt(choice) == 7) {
                return false;
            }

            // 2. Construct and marshall request
            ClientRequest clientRequest = ClientServices.getService(++MESSAGE_ID, choice, scanner);
            log.info("Client request: " + clientRequest.toString());
            byte[] marshalledRequest = MarshallUtil.marshall(clientRequest);
            log.info("Marshalled request: " + marshalledRequest);
            DatagramPacket requestPacket = new DatagramPacket(marshalledRequest, marshalledRequest.length);

            ServerReply responseObject = (ServerReply) handleRequest(requestPacket);
            log.info("Unmarshalled reply is: " + responseObject);
            displayServerReply(responseObject);
            
            // Handle flight monitoring
            if (Integer.parseInt(choice) == 4) {
                if (responseObject.getResponse().isEmpty()) {
                    System.out.println(responseObject.getServerMsg());
                }
                Monitoring monitor = (Monitoring) responseObject.getResponse().get();
                LocalDateTime expiry = monitor.getExpiry();
                handleCallback(expiry);
            }

        // we will reach here when the socket times out in the monitoring phase
        } catch (IOException e) {
            log.info("Monitoring has expired");
        }
        return true;
    }

    private void handleCallback(LocalDateTime expiryTime) throws IOException {
        while (true) {
            if (LocalDateTime.now().isBefore(expiryTime)) {
                socket.setSoTimeout((int) Duration.between(LocalDateTime.now(), expiryTime).toMillis());
            } else {
                socket.setSoTimeout(1);
            }
            byte[] replyBuffer = new byte[1024];
            DatagramPacket replyPacket = new DatagramPacket(replyBuffer, replyBuffer.length);
            socket.receive(replyPacket);

            byte[] marshalledReply = replyPacket.getData();
            ServerReply updatedFlight = (ServerReply) MarshallUtil.unmarshall(marshalledReply);
            log.info(updatedFlight.toString());
            displayServerReply(updatedFlight);
        }
    }

    private ServerReply receiveReply(DatagramPacket requestPacket) throws IOException {
        byte[] replyBuffer = new byte[1024];
        DatagramPacket replyPacket = new DatagramPacket(replyBuffer, replyBuffer.length);
        boolean receivedReply = false;

        // Listen for reply
        // If did not simulate a request message loss, then send request
        while (!receivedReply) {
            try {
                socket.receive(replyPacket);
                log.info("Reply received: " + replyPacket.getData());
                receivedReply = true;
            } catch (SocketTimeoutException e) {
                log.info("No reply received. Resending request...");
                sendRequest(socket, requestPacket);
            }
        }

        byte[] marshalledReply = replyPacket.getData();
        ServerReply unmarshalledReply = (ServerReply) MarshallUtil.unmarshall(marshalledReply);
        return unmarshalledReply;
    }

    private ServerReply handleRequest(DatagramPacket requestPacket) throws SocketException, IOException {
        socket.setSoTimeout(5000);
        sendRequest(socket, requestPacket);
        return receiveReply(requestPacket);
    }

    private void sendRequest(DatagramSocket socket, DatagramPacket requestPacket) throws IOException {
        if (!(Simulate.isFailure(ReqOrReplyEnum.REQUEST))) {
            socket.send(requestPacket);
        }
    }

    private void displayServerReply(ServerReply reply) {
        System.out.println();
        System.out.println(reply.getServerMsg());
        System.out.println(reply.getClientDisplay());
    }
}
