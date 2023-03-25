package com.example.demo.server.servant;

import java.util.Optional;

import com.example.demo.server.servant.models.Bookings;
import com.example.demo.server.servant.models.ClientID;

public interface BookingsInterface {
    //Service 3
    Optional<Bookings> AddFlightBooking(ClientID clientID, int flightID, int numSeats);

    //Service 5
    Optional<Bookings> DeleteFlightBooking(ClientID clientID, int flightID);

    //Service 6
    Optional<Bookings> UpdateFlightBooking(ClientID clientID, int flightID, int numSeats);
}
