package com.example.demo.server.servant;

import java.util.Optional;
import com.example.demo.server.servant.models.*;

public interface MonitoringInterface {
    //Service 4
    Optional<Monitoring> AddToMonitorList(ClientID clientID, int flightID, int monitorInterval);
}
