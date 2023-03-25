package com.example.demo.server.servant;

import java.util.Optional;
import com.example.demo.server.servant.models.*;

public interface MonitoringInterface {
    //Service 4
    Optional<Monitoring> AddToMonitorList(String clientIp, int clientPort, int flightId, int monitorDuration);
}
