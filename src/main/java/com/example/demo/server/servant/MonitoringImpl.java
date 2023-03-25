package com.example.demo.server.servant;

import com.example.demo.server.servant.models.*;
import com.example.demo.server.MessageService;
import com.example.demo.server.repositories.InformationRepository;
import com.example.demo.server.repositories.MonitoringRepository;

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
    public Optional<Monitoring> AddToMonitorList(ClientID clientID, int flightID, int monitorInterval) {
        LocalDateTime expiry = LocalDateTime.now().plusSeconds(monitorInterval);
        Optional<Information> existingFlight = this.informationRepository.findFlightsByFlightID(flightID);
        if (!existingFlight.isPresent()) {
            return Optional.empty();
        }

        Optional<Monitoring> existingMonitor = monitoringRepository.findFlightMonitoringByClientIDAndFlightID(clientID.getIP(), clientID.getPort(), flightID);
        if (existingMonitor.isPresent()) {
            monitoringRepository.updateFlightMonitoringByClientIDAndFlightID(clientID.getIP(), clientID.getPort(), flightID, expiry);
            return monitoringRepository.findFlightMonitoringByClientIDAndFlightID(clientID.getIP(), clientID.getPort(), flightID);
        }

        monitoringRepository.insertFlightMonitoringByClientIDAndFlightID(clientID.getIP(), clientID.getPort(), flightID, expiry);
        return monitoringRepository.findFlightMonitoringByClientIDAndFlightID(clientID.getIP(), clientID.getPort(), flightID);
    }

    public List<Monitoring> GetAllMonitorList() {
        return monitoringRepository.findAllFlightMonitoring();
    }

    public void SendUpdateToMonitorList(int flightID) {
        List<Monitoring> expiredMonitors = new ArrayList<>();
        List<Monitoring> monitors = monitoringRepository.findFlightMonitoringByFlightId(flightID);
        Information updatedFlight = informationRepository.findFlightsByFlightID(flightID).get();

        LocalDateTime now = LocalDateTime.now();
        for (Monitoring monitor : monitors) {
            if (now.isAfter(monitor.getExpiry())) {
                expiredMonitors.add(monitor);
            } else {
                messageService.sendMessageToClient(monitor.getClientID(), updatedFlight.toString().getBytes());
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
