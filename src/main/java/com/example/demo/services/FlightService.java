package com.example.demo.services;

import java.time.LocalDateTime;
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
        Optional<FlightInformation> existingFlight = GetFlightById(flightId);
        if (!existingFlight.isPresent()) {
            return 0;
        }

        Optional<FlightBookings> existingBooking = GetFlightBooking(clientIp, clientPort, flightId);
        if (existingBooking.isPresent()) {
            return 0;
        }

        int availableSeats = existingFlight.get().getSeatAvailability();
        int actualBookedSeats = numSeats <= availableSeats ? numSeats : numSeats - availableSeats;
        bookingsRepository.insertFlightBookings(clientIp, clientPort, flightId, actualBookedSeats);
        informationRepository.updateFlightsSeatAvailability(flightId, availableSeats - actualBookedSeats);
        SendUpdateToMonitorList(flightId);
        return actualBookedSeats;
    }

    //Service 5
    public int DeleteFlightBooking(String clientIp, int clientPort, int flightId) {
        Optional<FlightBookings> existingBooking = GetFlightBooking(clientIp, clientPort, flightId);
        if (!existingBooking.isPresent()) {
            return 0;
        }

        int releasedSeats = existingBooking.get().getNumSeats();
        bookingsRepository.deleteFlightBookings(clientIp, clientPort, flightId);

        Optional<FlightInformation> flight = informationRepository.findFlightsByFlightID(flightId);
        informationRepository.updateFlightsSeatAvailability(flightId, flight.get().getSeatAvailability() + releasedSeats);
        SendUpdateToMonitorList(flightId);
        return releasedSeats;
    }

    //Service 6
    public int UpdateFlightBooking(String clientIp, int clientPort, int flightId, int numSeats) {
        Optional<FlightBookings> existingBooking = GetFlightBooking(clientIp, clientPort, flightId);
        if (!existingBooking.isPresent()) {
            return 0;
        }

        Optional<FlightInformation> existingFlight = GetFlightById(flightId);
        int availableSeats = existingFlight.get().getSeatAvailability();
        int actualBookedSeats = numSeats <= availableSeats ? numSeats : numSeats - availableSeats;

        bookingsRepository.incrementFlightBookings(clientIp, clientPort, flightId, actualBookedSeats);
        informationRepository.updateFlightsSeatAvailability(flightId, availableSeats - actualBookedSeats);
        SendUpdateToMonitorList(flightId);
        return actualBookedSeats;
    }

    //Service 4
    public void AddToMonitorList(String clientIp, int clientPort, int flightId, LocalDateTime expiry) {
        Optional<FlightInformation> existingFlight = GetFlightById(flightId);
        if (!existingFlight.isPresent()) {
            return;
        }

        Optional<FlightMonitoring> existingMonitor = monitoringRepository.findFlightMonitoringByClientIDAndFlightID(clientIp, clientPort, flightId);
        if (existingMonitor.isPresent()) {
            monitoringRepository.updateFlightMonitoringByClientIDAndFlightID(clientIp, clientPort, flightId, expiry);
            return;
        }

        monitoringRepository.insertFlightMonitoringByClientIDAndFlightID(clientIp, clientPort, flightId, expiry);
    }

    public List<FlightMonitoring> GetMonitorList() {
        return monitoringRepository.findAllFlightMonitoring();
    }

    private void SendUpdateToMonitorList(int flightId) {
        List<FlightMonitoring> expiredMonitors = new ArrayList<>();
        List<FlightMonitoring> monitors = monitoringRepository.findFlightMonitoringByFlightId(flightId);
        LocalDateTime now = LocalDateTime.now();
        for (FlightMonitoring monitor : monitors) {
            if (now.isAfter(monitor.getExpiry())) {
                expiredMonitors.add(monitor);
            } else {
                SendUpdate(monitor);
            }
        }
        //lazy cleanup of expired monitoring channels
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
