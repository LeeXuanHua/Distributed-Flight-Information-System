package com.example.demo.client;

import com.example.demo.utils.InputValidator;
import com.example.demo.utils.MarshallUtil;
import com.example.demo.utils.ReqOrReplyEnum;
import com.example.demo.utils.Simulate;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
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
                System.out.println("Service 1: Lol");
                System.out.println("Service 2: Lmao");
                System.out.println("Service 3: xd");
                System.out.println("Service 4: lmfao");
                System.out.println("Service 5: rofl");
                System.out.println("Service 6: yuh");
                System.out.print("Make your selection (1-6): ");
                choice = scanner.next();
            } while (!InputValidator.isInteger(choice, Optional.of(1), Optional.of(6)));

            // 2. Construct and marshall request
            byte[] unmarshalledRequest = (++MESSAGE_ID + " | " + choice + " | goofy ahh message").getBytes();
            byte[] marshalledRequest = MarshallUtil.marshal(unmarshalledRequest);
            DatagramPacket requestPacket = new DatagramPacket(marshalledRequest, marshalledRequest.length);

            // 3. If did not simulate a request message loss, then send request
            if (!(Simulate.isFailure(ReqOrReplyEnum.REQUEST))) {
                socket.send(requestPacket);
                log.info("Sending request. The message before marshalling is: " + new String(unmarshalledRequest, StandardCharsets.UTF_8));
            }

            // 4. Listen for reply and unmarshall
            byte[] replyBuffer = new byte[1024];
            DatagramPacket replyPacket = new DatagramPacket(replyBuffer, replyBuffer.length);
            socket.receive(replyPacket);
            log.info("Reply received: " + new String(replyPacket.getData(), StandardCharsets.UTF_8).trim());
            byte[] marshalledReply = replyPacket.getData();
            byte[] unmarshalledReply = MarshallUtil.unmarshall(marshalledReply);

            // todo send request again if did not receive a reply, i.e. if step 4 did not run within TIMEOUT seconds, then run step 2 again

        // anything requiring a socket function will need to have this catch block
        } catch (IOException e) {
            log.error("IOError: " + e.getMessage());
        }
    }
}
