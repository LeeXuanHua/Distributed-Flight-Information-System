package com.example.demo.server.servant;

import java.util.List;
import java.util.Optional;

import com.example.demo.utils.InsufficientSeatsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.server.servant.models.Bookings;
import com.example.demo.server.servant.models.ClientID;
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

    public Optional<Bookings> GetFlightBooking(ClientID clientID, int flightID) {
        return bookingsRepository.findFlightBookingsByClientIDAndFlightID(clientID.getIP(), clientID.getPort(), flightID);
    }

    public List<Bookings> GetAllFlightBookings() {
        return bookingsRepository.findAllFlightBookings();
    }

    //Service 3
    @Override
    public Optional<Bookings> AddFlightBooking(ClientID clientID, int flightID, int numSeats) {
        Optional<Information> existingFlight = this.informationService.GetFlightById(flightID);
        if (!existingFlight.isPresent()) {
            return Optional.empty();
        }

        Optional<Bookings> existingBooking = GetFlightBooking(clientID, flightID);
        if (existingBooking.isPresent()) {
            return Optional.empty();
        }

        int availableSeats = existingFlight.get().getSeatAvailability();

        if (numSeats > availableSeats) {
            throw new InsufficientSeatsException();
        }

//        int actualBookedSeats = numSeats <= availableSeats ? numSeats : availableSeats ;
        bookingsRepository.insertFlightBookings(clientID.getIP(), clientID.getPort(), flightID, numSeats);
        informationRepository.updateFlightsSeatAvailability(flightID, availableSeats - numSeats);
        monitoringService.SendUpdateToMonitorList(flightID);
        return GetFlightBooking(clientID, flightID);
    }

    //Service 5
    @Override
    public Optional<Bookings> DeleteFlightBooking(ClientID clientID, int flightID) {
        Optional<Bookings> existingBooking = GetFlightBooking(clientID, flightID);
        if (!existingBooking.isPresent()) {
            return Optional.empty();
        }

        int releasedSeats = existingBooking.get().getNumSeats();
        bookingsRepository.deleteFlightBookings(clientID.getIP(), clientID.getPort(), flightID);

        Optional<Information> flight = informationRepository.findFlightsByFlightID(flightID);
        informationRepository.updateFlightsSeatAvailability(flightID, flight.get().getSeatAvailability() + releasedSeats);
        this.monitoringService.SendUpdateToMonitorList(flightID);
        return existingBooking;
    }

    //Service 6
    @Override
    public Optional<Bookings> UpdateFlightBooking(ClientID clientID, int flightID, int numSeats) {
        Optional<Bookings> existingBooking = GetFlightBooking(clientID, flightID);
        if (!existingBooking.isPresent()) {
            return Optional.empty();
        }

        Optional<Information> existingFlight = this.informationService.GetFlightById(flightID);
        int availableSeats = existingFlight.get().getSeatAvailability();
        int actualBookedSeats = numSeats <= availableSeats ? numSeats : availableSeats;

        bookingsRepository.incrementFlightBookings(clientID.getIP(), clientID.getPort(), flightID, actualBookedSeats);
        informationRepository.updateFlightsSeatAvailability(flightID, availableSeats - actualBookedSeats);
        this.monitoringService.SendUpdateToMonitorList(flightID);
        return GetFlightBooking(clientID, flightID);
    }

}
