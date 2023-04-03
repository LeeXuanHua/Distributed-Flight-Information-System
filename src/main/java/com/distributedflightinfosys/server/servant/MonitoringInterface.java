package com.distributedflightinfosys.server.servant;

import java.util.Optional;

import com.distributedflightinfosys.server.servant.models.ClientID;
import com.distributedflightinfosys.server.servant.models.Monitoring;

public interface MonitoringInterface {
    //Service 4
    Optional<Monitoring> AddToMonitorList(ClientID clientID, int flightID, int monitorInterval);
}
