package com.example.demo.server.invocation;

import com.example.demo.server.servant.BookingsImpl;
import com.example.demo.server.servant.InformationImpl;
import com.example.demo.server.servant.MonitoringImpl;
import com.example.demo.server.servant.models.ClientID;
import com.example.demo.client.ClientRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

@Slf4j
public class InvocationAtMostOnce extends Invocation {
    private HashMap<InvocationID, Object> InvocationHistory = new HashMap<>();

    public InvocationAtMostOnce(BookingsImpl bookings, MonitoringImpl monitoring, InformationImpl information) {
        super(bookings, monitoring, information);
    }

    @Override
    public Object handleRequest(String clientAddress, int clientPort, ClientRequest clientRequest) {
        ClientID clientID = new ClientID(clientAddress, clientPort);
        InvocationID invocationID = new InvocationID(clientID, clientRequest.getMessageId());
        Object res;
        if (InvocationHistory.containsKey(invocationID)) {
            res = InvocationHistory.get(invocationID);
            log.warn("!!! WARNING: Duplicate request detected. Returning cached reply.");
        } else {
            res = callServant(clientID, clientRequest.getServiceId(), clientRequest.getRequestBody());
            InvocationHistory.put(invocationID, res);
        }
        return res;
    }
}
