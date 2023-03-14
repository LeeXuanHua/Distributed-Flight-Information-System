package com.example.demo.server;

import com.example.demo.server.servant.InvocInterface;
import com.example.demo.utils.MarshallingUnmarshalling;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

@Slf4j
public class Server {

    // at-least-once and at-most-once semantics can be invoked when appropriate using strategy design pattern (.handleRequest())
    private InvocInterface invocInterface;
    private DatagramSocket socket;

    public Server(InvocInterface invocInterface, DatagramSocket socket) {
        this.invocInterface = invocInterface;
        this.socket = socket;
    }

    public void run() {
        try {
            // 1. Receive the packet from the client
            byte[] buffer = new byte[1024];
            DatagramPacket requestPacket = new DatagramPacket(buffer, buffer.length);
            socket.receive(requestPacket);

            // 2. Extract the request identifier ({IP address, port, requestId}) from the packet.
            //    To do this, we need to unmarshall the data at the same time
            InetAddress clientAddress = requestPacket.getAddress();
            int clientPort = requestPacket.getPort();
            String marshalledData = new String(requestPacket.getData()).trim();
            String unmarshalledData = marshalledData;
            //todo this will not work if the requestId is more than one digit i.e. >= 10
            //todo marshalled/unmarshalled data might need to be byte[] instead of string
            int clientRequestId = unmarshalledData.charAt(0);

            // 3. Let the invocInterface handle the request based on the required invocation semantic
            byte[] unmarshalledReply = invocInterface.handleRequest(clientAddress.getHostAddress(), clientRequestId, clientPort, unmarshalledData);
            byte[] marshalledReply = MarshallingUnmarshalling.marshal(unmarshalledReply);

            // 4. Send the reply, accounting for the chance of simulated failure
            DatagramPacket replyPacket = new DatagramPacket(marshalledReply, marshalledReply.length, clientAddress, clientPort);
            socket.send(replyPacket);

        } catch (IOException e) {
            log.error("IOError: " + e.getMessage());
        }
    }
}
