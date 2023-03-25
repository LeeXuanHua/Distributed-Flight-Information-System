package com.example.demo.server;

import com.example.demo.server.servant.BookingsImpl;
import com.example.demo.server.servant.InformationImpl;
import com.example.demo.server.servant.MonitoringImpl;
import com.example.demo.server.servant.models.ClientID;
import com.example.demo.server.servant.models.ClientRequest;

public class InvocationAtLeastOnce extends Invocation {
    public InvocationAtLeastOnce(BookingsImpl bookings, MonitoringImpl monitoring, InformationImpl information) {
        super(bookings, monitoring, information);
    }

    @Override
    public Object handleRequest(String clientAddress, int clientPort, ClientRequest clientRequest) {
        ClientID clientID = new ClientID(clientAddress, clientPort);
        return callServant(clientID, clientRequest.getServiceId(), clientRequest.getRequestBody());
    }
}
