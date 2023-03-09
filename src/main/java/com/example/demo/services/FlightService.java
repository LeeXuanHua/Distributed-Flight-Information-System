package com.example.demo.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.demo.models.FlightBookings;
import com.example.demo.models.FlightBookingsRepository;
import com.example.demo.models.FlightInformation;
import com.example.demo.models.FlightInformationRepository;
import com.example.demo.models.FlightMonitoring;
import com.example.demo.models.FlightMonitoringRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FlightService {
    private FlightInformationRepository informationRepository;
    private FlightBookingsRepository bookingsRepository;
    private FlightMonitoringRepository monitoringRepository;

    public FlightService(@Autowired FlightInformationRepository informationRepository,
                         @Autowired FlightBookingsRepository bookingsRepository,
                         @Autowired FlightMonitoringRepository monitoringRepository) {
        this.informationRepository = informationRepository;
        this.bookingsRepository = bookingsRepository;
        this.monitoringRepository = monitoringRepository;
    }

    //Service 1
    public List<FlightInformation> GetFlightsBySourceAndDestination(String src, String dest) {
        return informationRepository.findFlightsBySrcAndDest(src, dest);
    }

    //Service 2
    public Optional<FlightInformation> GetFlightById(int id) {
        return informationRepository.findFlightsByFlightID(id);
    }

    public List<FlightInformation> GetAllFlights() {
        return informationRepository.findAllFlights();
    }

    public Optional<FlightBookings> GetFlightBooking(String clientIp, int clientPort, int flightId) {
        return bookingsRepository.findFlightBookingsByClientIDAndFlightID(clientIp, clientPort, flightId);
    }

    public List<FlightBookings> GetAllFlightBookings() {
        return bookingsRepository.findAllFlightBookings();
    }

    //Service 3
    public int AddFlightBooking(String clientIp, int clientPort, int flightId, int numSeats) {
        Optional<FlightBookings> existingBooking = GetFlightBooking(clientIp, clientPort, flightId);
        if (existingBooking.isPresent()) {
            return 0;
        }

        int res = bookingsRepository.insertFlightBookings(clientIp, clientPort, flightId, numSeats);
        SendUpdateToMonitorList(flightId);
        return res;
    }

    //Service 5
    public int DeleteFlightBooking(String clientIp, int clientPort, int flightId) {
        Optional<FlightBookings> existingBooking = GetFlightBooking(clientIp, clientPort, flightId);
        if (existingBooking.isPresent()) {
            return 0;
        }

        int res = bookingsRepository.deleteFlightBookings(clientIp, clientPort, flightId);
        SendUpdateToMonitorList(flightId);
        return res;
    }

    //Service 6
    public int UpdateFlightBooking(String clientIp, int clientPort, int flightId, int numSeats) {
        Optional<FlightBookings> existingBooking = GetFlightBooking(clientIp, clientPort, flightId);
        if (existingBooking.isPresent()) {
            return 0;
        }

        int res = bookingsRepository.incrementFlightBookings(clientIp, clientPort, flightId, numSeats);
        SendUpdateToMonitorList(flightId);
        return res;
    }

    //Service 4
    public void AddToMonitorList(String clientIp, int clientPort, int flightId, int interval) {
        Optional<FlightMonitoring> existingMonitor = monitoringRepository.findFlightMonitoringByClientIDAndFlightID(clientIp, clientPort, flightId);
        if (existingMonitor.isPresent()) {
            monitoringRepository.updateFlightMonitoringByClientIDAndFlightID(clientIp, clientPort, flightId, interval);
            return;
        }

        monitoringRepository.insertFlightMonitoringByClientIDAndFlightID(clientIp, clientPort, flightId, interval);
    }

    private void SendUpdateToMonitorList(int flightId) {
        List<FlightMonitoring> expiredMonitors = new ArrayList<>();
        List<FlightMonitoring> monitors = monitoringRepository.findAllFlightMonitoring(flightId);
        for (FlightMonitoring monitor : monitors) {
            // TODO: add condition to filter expired monitors, ideally:
            // if (monitor.getExpiry() < currentTime) {
            //     expiredMonitors.add(monitor);
            // } else {
            //     SendUpdate(monitor);
            // }
        }

        CleanUpMonitorList(expiredMonitors);
    }

    private void SendUpdate(FlightMonitoring monitor) {
        //TODO: add logic to send updates
    }

    private void CleanUpMonitorList(List<FlightMonitoring> monitors) {
        for (FlightMonitoring monitor : monitors) {
            monitoringRepository.deleteFlightMonitoringByClientIDAndFlightID(
                monitor.getClientID().getIP(),
                monitor.getClientID().getPort(),
                monitor.getFlightID()
            );
        }
    }
}
