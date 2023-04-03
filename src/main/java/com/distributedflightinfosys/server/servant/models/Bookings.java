package com.distributedflightinfosys.server.servant.models;

import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Contains IP address, port, flight identifier, and number of reserved seats
 */
@Entity(name = "Flight_Bookings")
@Table(name = "flight_bookings", uniqueConstraints = {
        @UniqueConstraint(name = "uniqueClientID", columnNames = {"client_ip", "client_port"})
})
@NoArgsConstructor
@Getter
@Setter
public class Bookings implements DataEntity{
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

    @Override
    public String toString() {
        return "FlightBookings{" +
                "clientID=" + clientID +
                ", flightID=" + flightID +
                ", numSeats=" + numSeats +
                '}';
    }

    @Override
    public String getClientDisplay() {
        return  "Client ID = " + clientID + "\n" +
                "Flight ID = " + flightID + "\n" +
                "No. of Seats = " + numSeats + "\n";
    }
}
