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

public class FlightService {
    private FlightInformationRepository informationRepository;
    private FlightBookingsRepository bookingsRepository;
    private FlightMonitoringRepository monitoringRepository;

    //Service 1
    public List<FlightInformation> GetFlightBookingsBySourceAndDestination(String src, String dest) {
        return informationRepository.findFlightsBySrcAndDest(src, dest);
    }

    //Service 2
    public Optional<FlightInformation> GetFlightById(int id) {
        return informationRepository.findFlightsByFlightID(id);
    }

    //Service 3
    public int AddReservation(String clientId, int clientPort, int flightId, int numSeats) {
        Optional<FlightBookings> existingBooking = bookingsRepository.findFlightBookingsByClientIDAndFlightID(clientId, clientPort, flightId);
        if (existingBooking != null) {
            return 0;
        }

        int res = bookingsRepository.insertFlightBookings(clientId, clientPort, flightId, numSeats);
        SendUpdateToMonitorList(flightId);
        return res;
    }

    //Service 5
    public int DeleteReservation(String clientId, int clientPort, int flightId) {
        Optional<FlightBookings> existingBooking = bookingsRepository.findFlightBookingsByClientIDAndFlightID(clientId, clientPort, flightId);
        if (existingBooking != null) {
            return 0;
        }

        int res = bookingsRepository.deleteFlightBookings(clientId, clientPort, flightId);
        SendUpdateToMonitorList(flightId);
        return res;
    }

    //Service 6
    public int UpdateReservation(String clientId, int clientPort, int flightId, int numSeats) {
        Optional<FlightBookings> existingBooking = bookingsRepository.findFlightBookingsByClientIDAndFlightID(clientId, clientPort, flightId);
        if (existingBooking != null) {
            return 0;
        }

        int res = bookingsRepository.incrementFlightBookings(clientId, clientPort, flightId, numSeats);
        SendUpdateToMonitorList(flightId);
        return res;
    }

    //Service 4
    public void AddToMonitorList(String clientIP, int clientPort, int flightId, int interval) {
        Optional<FlightMonitoring> existingMonitor = monitoringRepository.findFlightMonitoringByClientIDAndFlightID(clientIP, clientPort, flightId);
        if (existingMonitor != null) {
            monitoringRepository.updateFlightMonitoringByClientIDAndFlightID(clientIP, clientPort, flightId, interval);
            return;
        }

        monitoringRepository.insertFlightMonitoringByClientIDAndFlightID(clientIP, clientPort, flightId, interval);
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
