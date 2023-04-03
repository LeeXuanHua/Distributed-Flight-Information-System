package com.distributedflightinfosys.server.servant;

import java.util.List;
import java.util.Optional;

import com.distributedflightinfosys.server.repositories.BookingsRepository;
import com.distributedflightinfosys.server.repositories.InformationRepository;
import com.distributedflightinfosys.server.servant.models.Bookings;
import com.distributedflightinfosys.server.servant.models.ClientID;
import com.distributedflightinfosys.server.servant.models.Information;
import com.distributedflightinfosys.utils.InsufficientSeatsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    // Helper function to get existing flight booking
    public Optional<Bookings> GetFlightBooking(ClientID clientID, int flightID) {
        return bookingsRepository.findFlightBookingsByClientIDAndFlightID(clientID.getIP(), clientID.getPort(), flightID);
    }

    // Helper function to get all existing flight bookings
    public List<Bookings> GetAllFlightBookings() {
        return bookingsRepository.findAllFlightBookings();
    }

    //Service 3 - Add Flight Booking
    @Override
    public Optional<Bookings> AddFlightBooking(ClientID clientID, int flightID, int numSeats) {
        // Check if flight with flightId exists.
        // If does not exist, service will exit and return an empty object
        Optional<Information> existingFlight = this.informationService.GetFlightById(flightID);
        if (!existingFlight.isPresent()) {
            return Optional.empty();
        }

        // Check if a booking for the same flightId by the requesting client exists.
        // If a booking exists, service will exit and return an empty object
        Optional<Bookings> existingBooking = GetFlightBooking(clientID, flightID);
        if (existingBooking.isPresent()) {
            return Optional.empty();
        }

        // Check if there are enough seats for the booking.
        // If there are insufficient seats, service will exit and return an empty object
        int availableSeats = existingFlight.get().getSeatAvailability();
        if (numSeats > availableSeats) {
            throw new InsufficientSeatsException();
        }

        // If there is a valid flight and no existing booking, can proceed to add a new booking
        bookingsRepository.insertFlightBookings(clientID.getIP(), clientID.getPort(), flightID, numSeats);
        // Update flight information with new seat availability
        informationRepository.updateFlightsSeatAvailability(flightID, availableSeats - numSeats);

        // Send updates to all clients monitoring the flight
        monitoringService.SendUpdateToMonitorList(flightID);
        return GetFlightBooking(clientID, flightID);
    }

    //Service 5 - Delete Flight Booking
    @Override
    public Optional<Bookings> DeleteFlightBooking(ClientID clientID, int flightID) {
        // Check if flight with flightId exists.
        // If does not exist, service will exit and return an empty object
        Optional<Bookings> existingBooking = GetFlightBooking(clientID, flightID);
        if (!existingBooking.isPresent()) {
            return Optional.empty();
        }

        // If there is a valid booking, can proceed to delete the booking
        int releasedSeats = existingBooking.get().getNumSeats();
        bookingsRepository.deleteFlightBookings(clientID.getIP(), clientID.getPort(), flightID);

        // Update flight information with new seat availability
        Optional<Information> flight = informationRepository.findFlightByFlightID(flightID);
        informationRepository.updateFlightsSeatAvailability(flightID, flight.get().getSeatAvailability() + releasedSeats);

        // Send updates to all clients monitoring the flight
        this.monitoringService.SendUpdateToMonitorList(flightID);
        return existingBooking;
    }

    //Service 6 - Increment Flight Booking
    @Override
    public Optional<Bookings> UpdateFlightBooking(ClientID clientID, int flightID, int numSeats) {
        // Check if flight with flightId exists.
        // If does not exist, service will exit and return an empty object
        Optional<Bookings> existingBooking = GetFlightBooking(clientID, flightID);
        if (!existingBooking.isPresent()) {
            return Optional.empty();
        }

        // Check if there are enough seats for the booking.
        // If there are insufficient seats, service will exit and return an empty object
        Optional<Information> existingFlight = this.informationService.GetFlightById(flightID);
        int availableSeats = existingFlight.get().getSeatAvailability();
        if (numSeats > availableSeats) {
            throw new InsufficientSeatsException();
        }

        // If there is a valid booking, can proceed to update the booking
        bookingsRepository.incrementFlightBookings(clientID.getIP(), clientID.getPort(), flightID, numSeats);
        // Update flight information with new seat availability
        informationRepository.updateFlightsSeatAvailability(flightID, availableSeats - numSeats);

        // Send updates to all clients monitoring the flight
        this.monitoringService.SendUpdateToMonitorList(flightID);
        return GetFlightBooking(clientID, flightID);
    }

}
