package com.example.demo.server;

import com.example.demo.client.ClientRequest;
import com.example.demo.client.ClientServices;
import com.example.demo.server.invocation.Invocation;
import com.example.demo.utils.MarshallUtil;
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

//        // TESTING: Service 1
//        ClientRequest clientRequest1 = new ClientRequest(1, 1,
//                ClientServices.Serialise("src","Singapore")+ClientServices.Serialise("dst","China"));
//        // TESTING: Service 2-1. Expected: Success, return object.
//        ClientRequest clientRequest21 = new ClientRequest(2, 2,
//                ClientServices.Serialise("flightID", "1"));
//        // TESTING: Service 2-2. Expected: ERROR - NO FLIGHTID
//        ClientRequest clientRequest22 = new ClientRequest(2,2,
//                ClientServices.Serialise("flightID", "2942190"));
//        // TESTING: Service 3-1. Expected: Success, return object.
//        ClientRequest clientRequest31 = new ClientRequest(3,3,
//                ClientServices.Serialise("flightID", "1")+ClientServices.Serialise("numSeats", "1"));
//        // TESTING: Service 3-2. Expected: ERROR - NO FLIGHTID
//        ClientRequest clientRequest32 = new ClientRequest(3,3,
//                ClientServices.Serialise("flightID", "2180921")+ClientServices.Serialise("numSeats", "1"));
//        // TESTING: Service 3-3. Expected: ERROR - INSUFF SEATS
//        ClientRequest clientRequest33 = new ClientRequest(3,3,
//                ClientServices.Serialise("flightID", "1")+ClientServices.Serialise("numSeats", "9999"));
//        // TESTING: Service 4. Expected: Success, return object.
//        ClientRequest clientRequest4 = new ClientRequest(4,4,
//                ClientServices.Serialise("flightID", "1")+ClientServices.Serialise("monitorInterval", "20"));
//        // TESTING: Service 5. Expected: Success, return object
//        ClientRequest clientRequest51 = new ClientRequest(5,5,
//                ClientServices.Serialise("flightID", "1"));
//        // TESTING: Service 5. Expected: ERROR - no such booking
//        ClientRequest clientRequest52 = new ClientRequest(5,5,
//                ClientServices.Serialise("flightID", "21321"));
//        // TESTING: Service 6.
//        ClientRequest clientRequest6 = new ClientRequest(6,6,
//                ClientServices.Serialise("flightID", "1")+ ClientServices.Serialise("numSeats", "1"));
//        log.info("Server received data:" + clientRequest1);
//        Object unmarshalledReply = invocation.handleRequest("12345", 8080, clientRequest1);
//        log.info("reply: " + unmarshalledReply);
    }
}
