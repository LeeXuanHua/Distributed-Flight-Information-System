package com.example.demo.client;

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
                System.out.println("1: Get Flights by Source & Destination");
                System.out.println("2: Get Flights by ID");
                System.out.println("3: Book seats on a flight");
                System.out.println("4: Monitor flight availability");
                System.out.println("5: Delete flight booking");
                System.out.println("6: Update flight booking");
                System.out.println("7: Terminate client");
                System.out.print("Make your selection (1-7): ");
                choice = scanner.next();
            } while (!InputValidator.isInteger(choice, Optional.of(1), Optional.of(6)));

            if (Integer.parseInt(choice) == 7) {
                return;
            }

            // 2. Construct and marshall request
            ClientRequest clientRequest = ClientServices.getService(++MESSAGE_ID, choice, scanner);
            log.info("Client request: " + clientRequest.toString());
            byte[] marshalledRequest = MarshallUtil.marshall(clientRequest);
            log.info("Marshalled request: " + marshalledRequest);
            DatagramPacket requestPacket = new DatagramPacket(marshalledRequest, marshalledRequest.length);

            Object responseObject = handleRequest(requestPacket);
            log.info("Unmarshalled reply is: " + responseObject);
            
            // Handle flight monitoring
            if (Integer.parseInt(choice) == 4) {
                Optional<Monitoring> monitor = (Optional<Monitoring>) responseObject;
                if (monitor.isPresent()) {
                    LocalDateTime expiry = monitor.get().getExpiry();
                    handleCallback(expiry);
                } else {
                    System.out.println("Unable to monitor flight because it does not exist");
                }                
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
                log.info("Reply received: " + replyPacket.getData());
                receivedReply = true;
            } catch (SocketTimeoutException e) {
                log.info("No reply received. Resending request...");
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
