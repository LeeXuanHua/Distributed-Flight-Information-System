package com.distributedflightinfosys.server.servant;

import com.distributedflightinfosys.server.repositories.InformationRepository;
import com.distributedflightinfosys.server.repositories.MonitoringRepository;
import com.distributedflightinfosys.server.servant.models.ClientID;
import com.distributedflightinfosys.server.servant.models.Information;
import com.distributedflightinfosys.server.servant.models.Monitoring;
import com.distributedflightinfosys.server.ServerReply;
import com.distributedflightinfosys.server.MessageService;

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

    public MonitoringImpl(@Autowired InformationRepository informationRepository,
                          @Autowired MonitoringRepository monitoringRepository) {
        this.informationRepository = informationRepository;
        this.monitoringRepository = monitoringRepository;
    }

    //Service 4 - Add to Monitor List
    @Override
    public Optional<Monitoring> AddToMonitorList(ClientID clientID, int flightID, int monitorInterval) {
        // Calculate expiry based on current time and monitorInterval
        LocalDateTime expiry = LocalDateTime.now().plusSeconds(monitorInterval);

        // Check if flight with flightId exists.
        // If does not exist, service will exit and return an empty object
        Optional<Information> existingFlight = this.informationRepository.findFlightByFlightID(flightID);
        if (!existingFlight.isPresent()) {
            return Optional.empty();
        }

        // Check if monitor with same flightId exists.
        // If it exists, update existing Monitoring object with new expiry
        // Update can only be triggered by At-least-once invocation 
        Optional<Monitoring> existingMonitor = monitoringRepository.findFlightMonitoringByClientIDAndFlightID(clientID.getIP(), clientID.getPort(), flightID);
        if (existingMonitor.isPresent()) {
            monitoringRepository.updateFlightMonitoringByClientIDAndFlightID(clientID.getIP(), clientID.getPort(), flightID, expiry);
            return monitoringRepository.findFlightMonitoringByClientIDAndFlightID(clientID.getIP(), clientID.getPort(), flightID);
        }

        // If does not exist, can create new Monitoring object
        monitoringRepository.insertFlightMonitoringByClientIDAndFlightID(clientID.getIP(), clientID.getPort(), flightID, expiry);
        return monitoringRepository.findFlightMonitoringByClientIDAndFlightID(clientID.getIP(), clientID.getPort(), flightID);
    }

    public List<Monitoring> GetAllMonitorList() {
        return monitoringRepository.findAllFlightMonitoring();
    }

    // Callback function to update all clients monitoring the updated flight
    public void SendUpdateToMonitorList(int flightID) {
        List<Monitoring> expiredMonitors = new ArrayList<>();
        List<Monitoring> monitors = monitoringRepository.findFlightMonitoringByFlightId(flightID);
        Information updatedFlight = informationRepository.findFlightByFlightID(flightID).get();

        LocalDateTime now = LocalDateTime.now();
        for (Monitoring monitor : monitors) {
            // Check for expired monitors.
            // If expired, add to expiredMonitors list to be deleted later
            if (now.isAfter(monitor.getExpiry())) {
                expiredMonitors.add(monitor);
            // If not expired, send update through a ServerReply
            } else {
                ServerReply reply = new ServerReply(true, "New monitoring update: ", Optional.of(updatedFlight));
                MessageService.getInstance().sendMessageToClient(monitor.getClientID(), reply);
            }
        }
        //lazy cleanup of expired monitoring channels
        CleanUpMonitorList(expiredMonitors);
    }

    // Helper method to delete exipred monitors 
    private void CleanUpMonitorList(List<Monitoring> monitors) {
        // Iteratively delete each monitor from repository 
        for (Monitoring monitor : monitors) {
            monitoringRepository.deleteFlightMonitoringByClientIDAndFlightID(
                monitor.getClientID().getIP(),
                monitor.getClientID().getPort(),
                monitor.getFlightID()
            );
        }
    }
}
