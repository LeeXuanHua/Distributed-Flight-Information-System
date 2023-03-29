package com.example.demo.server.invocation;

import com.example.demo.server.ServerReply;
import com.example.demo.server.servant.BookingsImpl;
import com.example.demo.server.servant.InformationImpl;
import com.example.demo.server.servant.MonitoringImpl;
import com.example.demo.server.servant.models.ClientID;
import com.example.demo.client.ClientRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

@Slf4j
public class InvocationAtMostOnce extends Invocation {
    private HashMap<InvocationID, ServerReply> InvocationHistory = new HashMap<>();

    public InvocationAtMostOnce(BookingsImpl bookings, MonitoringImpl monitoring, InformationImpl information) {
        super(bookings, monitoring, information);
    }

    @Override
    public ServerReply handleRequest(String clientAddress, int clientPort, ClientRequest clientRequest) {
        // Need to keep track of previous requests 
        // Previous requests are tracked using InvocationID - which takes into account clientID and messageID
        // InvocationIDs previously seen are stored in InvocationHistory hashmap
        ClientID clientID = new ClientID(clientAddress, clientPort);
        InvocationID invocationID = new InvocationID(clientID, clientRequest.getMessageId());
        ServerReply res;
        if (InvocationHistory.containsKey(invocationID)) {
            // InvocationID was previously seen, means the request was resent by the same client.
            res = InvocationHistory.get(invocationID);
            log.warn("!!! WARNING: Duplicate request detected. Returning cached reply.");
        } else {
            // InvocationID is new, can just call services and store InvocationID
            res = callServant(clientID, clientRequest.getServiceId(), clientRequest.getRequestBody());
            InvocationHistory.put(invocationID, res);
        }
        return res;
    }
}
