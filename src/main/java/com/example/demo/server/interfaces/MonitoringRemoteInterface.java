package com.example.demo.server.interfaces;

import java.time.LocalDateTime;

public interface MonitoringRemoteInterface {
    //Service 4
    void AddToMonitorList(String clientIp, int clientPort, int flightId, LocalDateTime expiry);
}
