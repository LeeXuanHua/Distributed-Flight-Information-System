package com.example.demo.server.servant;

import java.util.Optional;

import com.example.demo.server.servant.models.Bookings;

public interface BookingsInterface {
    //Service 3
    Optional<Bookings> AddFlightBooking(String clientIp, int clientPort, int flightId, int numSeats);

    //Service 5
    Optional<Bookings> DeleteFlightBooking(String clientIp, int clientPort, int flightId);

    //Service 6
    Optional<Bookings> UpdateFlightBooking(String clientIp, int clientPort, int flightId, int numSeats);
}
