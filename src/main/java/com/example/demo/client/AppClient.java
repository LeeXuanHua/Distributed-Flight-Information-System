package com.example.demo.client;

import com.example.demo.server.servant.InvocInterface;
import com.example.demo.utils.InputValidator;
import com.example.demo.utils.MarshallUtil;
import com.example.demo.utils.ReqOrReplyEnum;
import com.example.demo.utils.Simulate;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Scanner;

@Slf4j
public class AppClient {
    private InvocInterface invocInterface;
    DatagramSocket socket;

    int MESSAGE_ID;

    // TODO: implement invoc semantics logic
    public AppClient(InvocInterface invocInterface, DatagramSocket socket) {
        this.socket = socket;
        this.MESSAGE_ID = 0;
        this.invocInterface = invocInterface;
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

            String requestString = ClientServices.getService(choice, scanner);
            // 2. Construct and marshall request
            byte[] unmarshalledRequest = (++MESSAGE_ID + " | " + choice + " | " + requestString).getBytes();
            byte[] marshalledRequest = MarshallUtil.marshal(unmarshalledRequest);
            DatagramPacket requestPacket = new DatagramPacket(marshalledRequest, marshalledRequest.length);

            // 3. If did not simulate a request message loss, then send request
            if (!(Simulate.isFailure(ReqOrReplyEnum.REQUEST))) {
                socket.send(requestPacket);
                log.info("Sending request. The message before marshalling is: " + new String(unmarshalledRequest, StandardCharsets.UTF_8));
            }

            // 4. Listen for reply and unmarshall
            byte[] unmarshalledReply = receiveMessage();

            // todo send request again if did not receive a reply, i.e. if step 4 did not run within TIMEOUT seconds, then run step 2 again
            
            // Handle flight monitoring
            if (Integer.parseInt(choice) == 4) {
                LocalDateTime expiry = LocalDateTime.now().plusSeconds(30); //temporarily hardcoded. TODO: get expiry time from unmarshalled reply, check w xh
                handleCallback(expiry);
            }

        // anything requiring a socket function will need to have this catch block
        } catch (IOException e) {
            log.error("IOError: " + e.getMessage());
        }
    }

    public void handleCallback(LocalDateTime expiryTime) throws IOException {
        while (LocalDateTime.now().isBefore(expiryTime)) {
            byte[] unmarshalledReply = receiveMessage();
            // TODO: check w xh how to parse to objects
        }
    }

    public byte[] receiveMessage() throws IOException {
        byte[] replyBuffer = new byte[1024];
        DatagramPacket replyPacket = new DatagramPacket(replyBuffer, replyBuffer.length);
        socket.receive(replyPacket);
        log.info("Reply received: " + new String(replyPacket.getData(), StandardCharsets.UTF_8).trim());
        byte[] marshalledReply = replyPacket.getData();
        byte[] unmarshalledReply = MarshallUtil.unmarshall(marshalledReply);

        return unmarshalledReply;
    }
}
