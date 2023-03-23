package com.example.demo.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

import com.example.demo.models.ClientID;
import com.example.demo.utils.MarshallUtil;
import com.example.demo.utils.ReqOrReplyEnum;
import com.example.demo.utils.Simulate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageService {
    private static MessageService INSTANCE = null;
    private DatagramSocket socket;

    public MessageService(DatagramSocket socket) throws Exception {
        if (INSTANCE != null) throw new Exception("MessageService has already been initialised.");
        this.socket = socket;
        INSTANCE = this;
    }

    public static MessageService getInstance() {
        return INSTANCE;
    }

    public void sendMessageToClient(InetAddress clientAddress, int clientPort, byte[] unmarshalledMessage) {
        try {
            byte[] marshalledMessage = MarshallUtil.marshall(unmarshalledMessage);

            // Send message, accounting for the chance of simulated failure
            DatagramPacket messagePacket = new DatagramPacket(marshalledMessage, marshalledMessage.length, clientAddress, clientPort);
            if (!(Simulate.isFailure(ReqOrReplyEnum.REPLY))) {
                socket.send(messagePacket);
                log.info("Sending reply. The message before marshalling is: " + new String(unmarshalledMessage, StandardCharsets.UTF_8));
            }

        // anything requiring a socket function will need to have this catch block
        } catch (IOException e) {
            log.error("IOError: " + e.getMessage());
        }
    }

    public void sendMessageToClient(ClientID clientId, byte[] unmarshalledMessage) {
        try {
            sendMessageToClient(InetAddress.getByName(clientId.getIP()), clientId.getPort(), unmarshalledMessage);
        } catch (UnknownHostException e) {
            log.error("UnknownHostException: " + e.getMessage());
        } 
    }
}
