package com.example.demo.server.servant;

public interface BookingsInterface {
    //Service 3
    int AddFlightBooking(String clientIp, int clientPort, int flightId, int numSeats);

    //Service 5
    int DeleteFlightBooking(String clientIp, int clientPort, int flightId);

    //Service 6
    int UpdateFlightBooking(String clientIp, int clientPort, int flightId, int numSeats);
}
