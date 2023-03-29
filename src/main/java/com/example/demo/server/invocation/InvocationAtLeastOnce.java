package com.example.demo.server.invocation;

import com.example.demo.server.ServerReply;
import com.example.demo.server.servant.BookingsImpl;
import com.example.demo.server.servant.InformationImpl;
import com.example.demo.server.servant.MonitoringImpl;
import com.example.demo.server.servant.models.ClientID;
import com.example.demo.client.ClientRequest;

public class InvocationAtLeastOnce extends Invocation {
    public InvocationAtLeastOnce(BookingsImpl bookings, MonitoringImpl monitoring, InformationImpl information) {
        super(bookings, monitoring, information);
    }

    @Override
    public ServerReply handleRequest(String clientAddress, int clientPort, ClientRequest clientRequest) {
        // Do not need to keep track of previous requests 
        // Can just call the services directly without overhead
        ClientID clientID = new ClientID(clientAddress, clientPort);
        return callServant(clientID, clientRequest.getServiceId(), clientRequest.getRequestBody());
    }
}
