package com.distributedflightinfosys.server;

import com.distributedflightinfosys.server.invocation.Invocation;
import com.distributedflightinfosys.client.ClientRequest;
import com.distributedflightinfosys.utils.MarshallUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

@Slf4j
public class AppServer {

    public static int PORT = 2222;

    // at-least-once and at-most-once semantics can be invoked when appropriate using strategy design pattern (.handleRequest())
    private Invocation invocation;
    private DatagramSocket socket;
    private MessageService messageService;

    public AppServer(Invocation invocation, DatagramSocket socket) {
        this.invocation = invocation;
        this.socket = socket;
        try {
            this.messageService = new MessageService(socket);
        } catch (Exception e) {
            this.messageService = MessageService.getInstance();
        }
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
            log.info("Server received data: " + requestPacket.getData());
            ClientRequest clientRequest = (ClientRequest) MarshallUtil.unmarshall(requestPacket.getData());
            log.info("Server unmarshalling data as: " + clientRequest);

            // 3. With the client IP and port, ClientRequest, and requestString,
            //    let the invocInterface handle the request based on the required invocation semantic
            ServerReply unmarshalledReply = invocation.handleRequest(clientAddress.getHostAddress(), clientPort, clientRequest);

            // 4. Marshall the reply and send
            messageService.sendMessageToClient(clientAddress, clientPort, unmarshalledReply);

        // anything requiring a socket function will need to have this catch block
        } catch (IOException e) {
            log.error("33 IOError: " + e.getMessage());
        }
    }
}
