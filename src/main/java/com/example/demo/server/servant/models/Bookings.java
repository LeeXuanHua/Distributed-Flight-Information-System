package com.example.demo.server.servant.models;

import javax.persistence.*;

/**
 * Contains IP address, port, flight identifier, and number of reserved seats
 */
@Entity(name = "Flight_Bookings")
@Table(name = "flight_bookings", uniqueConstraints = {
        @UniqueConstraint(name = "uniqueClientID", columnNames = {"client_ip", "client_port"})
})
public class Bookings {
    @EmbeddedId
    private ClientID clientID;

    @Column(name = "flight_identifier", nullable = false)
    private int flightID;
    @Column(name = "number_of_reserved_seats", nullable = false)
    private int numSeats;

    public Bookings(ClientID clientID, int flightID, int numSeats) {
        this.clientID = clientID;
        this.flightID = flightID;
        this.numSeats = numSeats;
    }

    public Bookings() {}

    public ClientID getClientID() {
        return clientID;
    }

    public void setClientID(ClientID clientID) {
        this.clientID = clientID;
    }

    public int getFlightID() {
        return flightID;
    }

    public void setFlightID(int flightID) {
        this.flightID = flightID;
    }

    public int getNumSeats() {
        return numSeats;
    }

    public void setNumSeats(int numSeats) {
        this.numSeats = numSeats;
    }

    @Override
    public String toString() {
        return "FlightBookings{" +
                "clientID=" + clientID +
                ", flightID=" + flightID +
                ", numSeats=" + numSeats +
                '}';
    }
}
