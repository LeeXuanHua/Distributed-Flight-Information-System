package com.distributedflightinfosys.server.invocation;

import com.distributedflightinfosys.server.servant.models.ClientID;
import com.distributedflightinfosys.server.ServerReply;
import com.distributedflightinfosys.server.servant.BookingsImpl;
import com.distributedflightinfosys.server.servant.InformationImpl;
import com.distributedflightinfosys.server.servant.MonitoringImpl;
import com.distributedflightinfosys.client.ClientRequest;

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
