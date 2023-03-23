package com.example.demo.server.servant;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.server.servant.models.Bookings;
import com.example.demo.server.servant.models.Information;
import com.example.demo.server.repositories.BookingsRepository;
import com.example.demo.server.repositories.InformationRepository;

@Service
public class BookingsImpl implements BookingsInterface {
    private InformationImpl informationService;
    private BookingsRepository bookingsRepository;
    private InformationRepository informationRepository;
    private MonitoringImpl monitoringService;

    public BookingsImpl(@Autowired BookingsRepository bookingsRepository,
                        @Autowired InformationImpl informationService,
                        @Autowired InformationRepository informationRepository,
                        @Autowired MonitoringImpl monitoringService) {
        this.bookingsRepository = bookingsRepository;
        this.informationService = informationService;
        this.informationRepository = informationRepository;
        this.monitoringService = monitoringService;
    }

    public Optional<Bookings> GetFlightBooking(String clientIp, int clientPort, int flightId) {
        return bookingsRepository.findFlightBookingsByClientIDAndFlightID(clientIp, clientPort, flightId);
    }

    public List<Bookings> GetAllFlightBookings() {
        return bookingsRepository.findAllFlightBookings();
    }

    //Service 3
    @Override
    public int AddFlightBooking(String clientIp, int clientPort, int flightId, int numSeats) {
        Optional<Information> existingFlight = this.informationService.GetFlightById(flightId);
        if (!existingFlight.isPresent()) {
            return 0;
        }

        Optional<Bookings> existingBooking = GetFlightBooking(clientIp, clientPort, flightId);
        if (existingBooking.isPresent()) {
            return 0;
        }

        int availableSeats = existingFlight.get().getSeatAvailability();
        int actualBookedSeats = numSeats <= availableSeats ? numSeats : numSeats - availableSeats;
        bookingsRepository.insertFlightBookings(clientIp, clientPort, flightId, actualBookedSeats);
        this.informationRepository.updateFlightsSeatAvailability(flightId, availableSeats - actualBookedSeats);
        this.monitoringService.SendUpdateToMonitorList(flightId);
        return actualBookedSeats;
    }

    //Service 5
    @Override
    public int DeleteFlightBooking(String clientIp, int clientPort, int flightId) {
        Optional<Bookings> existingBooking = GetFlightBooking(clientIp, clientPort, flightId);
        if (!existingBooking.isPresent()) {
            return 0;
        }

        int releasedSeats = existingBooking.get().getNumSeats();
        bookingsRepository.deleteFlightBookings(clientIp, clientPort, flightId);

        Optional<Information> flight = informationRepository.findFlightsByFlightID(flightId);
        informationRepository.updateFlightsSeatAvailability(flightId, flight.get().getSeatAvailability() + releasedSeats);
        this.monitoringService.SendUpdateToMonitorList(flightId);
        return releasedSeats;
    }

    //Service 6
    @Override
    public int UpdateFlightBooking(String clientIp, int clientPort, int flightId, int numSeats) {
        Optional<Bookings> existingBooking = GetFlightBooking(clientIp, clientPort, flightId);
        if (!existingBooking.isPresent()) {
            return 0;
        }

        Optional<Information> existingFlight = this.informationService.GetFlightById(flightId);
        int availableSeats = existingFlight.get().getSeatAvailability();
        int actualBookedSeats = numSeats <= availableSeats ? numSeats : availableSeats;

        bookingsRepository.incrementFlightBookings(clientIp, clientPort, flightId, actualBookedSeats);
        informationRepository.updateFlightsSeatAvailability(flightId, availableSeats - actualBookedSeats);
        this.monitoringService.SendUpdateToMonitorList(flightId);
        return actualBookedSeats;
    }

}
