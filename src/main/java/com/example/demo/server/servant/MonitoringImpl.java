package com.example.demo.server.servant;

import com.example.demo.server.MessageService;
import com.example.demo.server.servant.models.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MonitoringImpl implements MonitoringInterface {
    private InformationRepository informationRepository;
    private MonitoringRepository monitoringRepository;
    private MessageService messageService;

    public MonitoringImpl(@Autowired InformationRepository informationRepository,
                          @Autowired MonitoringRepository monitoringRepository) {
        this.informationRepository = informationRepository;
        this.monitoringRepository = monitoringRepository;
        this.messageService = MessageService.getInstance();
    }

    //Service 4
    @Override
    public void AddToMonitorList(String clientIp, int clientPort, int flightId, int monitorDuration) {
        LocalDateTime expiry = LocalDateTime.now().plusSeconds(monitorDuration);
        Optional<Information> existingFlight = this.informationRepository.findFlightsByFlightID(flightId);
        if (!existingFlight.isPresent()) {
            return;
        }

        Optional<Monitoring> existingMonitor = monitoringRepository.findFlightMonitoringByClientIDAndFlightID(clientIp, clientPort, flightId);
        if (existingMonitor.isPresent()) {
            monitoringRepository.updateFlightMonitoringByClientIDAndFlightID(clientIp, clientPort, flightId, expiry);
            return;
        }

        monitoringRepository.insertFlightMonitoringByClientIDAndFlightID(clientIp, clientPort, flightId, expiry);
    }

    public List<Monitoring> GetMonitorList() {
        return monitoringRepository.findAllFlightMonitoring();
    }

    void SendUpdateToMonitorList(int flightId) {
        List<Monitoring> expiredMonitors = new ArrayList<>();
        List<Monitoring> monitors = monitoringRepository.findFlightMonitoringByFlightId(flightId);
        LocalDateTime now = LocalDateTime.now();
        for (Monitoring monitor : monitors) {
            if (now.isAfter(monitor.getExpiry())) {
                expiredMonitors.add(monitor);
            } else {
                messageService.sendMessageToClient(monitor.getClientID(), monitor.toString().getBytes());
            }
        }
        //lazy cleanup of expired monitoring channels
        CleanUpMonitorList(expiredMonitors);
    }

    private void CleanUpMonitorList(List<Monitoring> monitors) {
        for (Monitoring monitor : monitors) {
            monitoringRepository.deleteFlightMonitoringByClientIDAndFlightID(
                monitor.getClientID().getIP(),
                monitor.getClientID().getPort(),
                monitor.getFlightID()
            );
        }
    }
}
