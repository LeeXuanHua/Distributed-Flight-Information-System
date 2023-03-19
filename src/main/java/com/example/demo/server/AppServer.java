package com.example.demo.server;

import com.example.demo.server.servant.InvocInterface;
import com.example.demo.utils.MarshallUtil;
import com.example.demo.utils.ReqOrReplyEnum;
import com.example.demo.utils.Simulate;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

@Slf4j
public class AppServer {

    // at-least-once and at-most-once semantics can be invoked when appropriate using strategy design pattern (.handleRequest())
    private InvocInterface invocInterface;
    private DatagramSocket socket;

    public AppServer(InvocInterface invocInterface, DatagramSocket socket) {
        this.invocInterface = invocInterface;
        this.socket = socket;
    }

    public void run() {
        try {
            // 1. Receive the packet from the client
            byte[] buffer = new byte[1024];
            DatagramPacket requestPacket = new DatagramPacket(buffer, buffer.length);
            socket.receive(requestPacket);
            log.info("Request received: " + new String(requestPacket.getData(), StandardCharsets.UTF_8).trim());

            // 2. Extract the request identifier ({IP address, port, requestId}) from the packet.
            //    To do this, we need to unmarshall the data at the same time
            InetAddress clientAddress = requestPacket.getAddress();
            int clientPort = requestPacket.getPort();
            String lol = new String(requestPacket.getData(), StandardCharsets.UTF_8).trim();
            byte[] marshalledData = lol.getBytes();
            byte[] unmarshalledData = MarshallUtil.unmarshall(marshalledData);
            //todo this will not work if the requestId is more than one digit i.e. >= 10
            int clientRequestId = unmarshalledData.toString().charAt(0);

            // 3. Let the invocInterface handle the request based on the required invocation semantic
            //    Then, marshall the reply
            byte[] unmarshalledReply = invocInterface.handleRequest(clientAddress.getHostAddress(), clientRequestId, clientPort, lol);
            byte[] marshalledReply = MarshallUtil.marshal(unmarshalledReply);

            // 4. Send the reply, accounting for the chance of simulated failure
            DatagramPacket replyPacket = new DatagramPacket(marshalledReply, marshalledReply.length, clientAddress, clientPort);
            if (!(Simulate.isFailure(ReqOrReplyEnum.REPLY))) {
                socket.send(replyPacket);
                log.info("Sending reply. The message before marshalling is: " + new String(unmarshalledReply, StandardCharsets.UTF_8));
            }

        // anything requiring a socket function will need to have this catch block
        } catch (IOException e) {
            log.error("IOError: " + e.getMessage());
        }
    }
}
