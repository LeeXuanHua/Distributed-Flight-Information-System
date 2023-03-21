package com.example.demo.server.servant;

import java.time.LocalDateTime;

public interface MonitoringInterface {
    //Service 4
    void AddToMonitorList(String clientIp, int clientPort, int flightId, int monitorDuration);
}
