package com.distributedflightinfosys.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.distributedflightinfosys.server.servant.models.ClientID;
import com.distributedflightinfosys.utils.MarshallUtil;
import com.distributedflightinfosys.utils.ReqOrReplyEnum;
import com.distributedflightinfosys.utils.Simulate;

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

    public void sendMessageToClient(InetAddress clientAddress, int clientPort, ServerReply unmarshalledMessage) {
        try {
            log.info("The reply message before marshalling is: " + unmarshalledMessage);

            byte[] marshalledMessage = MarshallUtil.marshall(unmarshalledMessage);
            // Send message, accounting for the chance of simulated failure
            DatagramPacket messagePacket = new DatagramPacket(marshalledMessage, marshalledMessage.length, clientAddress, clientPort);
            if (!(Simulate.isFailure(ReqOrReplyEnum.REPLY))) {
                socket.send(messagePacket);
                log.info("Sending reply");
            }

        // anything requiring a socket function will need to have this catch block
        } catch (IOException e) {
            log.error("11 IOError: " + e.getMessage());
        }
    }

    public void sendMessageToClient(ClientID clientID, ServerReply unmarshalledMessage) {
        try {
            sendMessageToClient(InetAddress.getByName(clientID.getIP()), clientID.getPort(), unmarshalledMessage);
        } catch (UnknownHostException e) {
            log.error("UnknownHostException: " + e.getMessage());
        } 
    }
}
