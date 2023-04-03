package com.distributedflightinfosys.server.servant.models;

import javax.persistence.*;

import com.distributedflightinfosys.utils.StringHelper;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Contains flight identifier, source, destination, departure time, airfare, and seat availability
 */
@Entity(name="Flight_Information")
@Table(name="flight_information")
@NoArgsConstructor
@Getter
@Setter
public class Information implements DataEntity {
    @Id
    @Column(name = "flight_identifier", unique = true, nullable = false)
    private int flightID;
    @Column(name = "source", nullable = false)
    private String src;
    @Column(name = "destination", nullable = false)
    private String dest;
    @Column(name = "departure_time", nullable = false)
    private LocalDateTime departureTime;
    @Column(name = "airfare", nullable = false)
    private double airfare;
    @Column(name = "seat_availability", nullable = false)
    private int seatAvailability;

    public Information(int flightID, String src, String dest, LocalDateTime departureTime, double airfare, int seatAvailability) {
        this.flightID = flightID;
        this.src = src;
        this.dest = dest;
        this.departureTime = departureTime;
        this.airfare = airfare;
        this.seatAvailability = seatAvailability;
    }

    @Override
    public String toString() {
        return "FlightInformation{" +
                "flightID='" + flightID + '\'' +
                ", src='" + src + '\'' +
                ", dest='" + dest + '\'' +
                ", departureTime='" + departureTime + '\'' +
                ", airfare=" + airfare +
                ", seatAvailability=" + seatAvailability +
                '}';
    }

    @Override
    public String getClientDisplay() {
        return  "Flight ID = " + flightID + "\n" +
                "Source = " + src + "\n" +
                "Destination = " + dest + "\n" +
                "Departure Time = " + StringHelper.formatLocalDateTime(departureTime) + "\n" +
                "Airfare = " + StringHelper.formatCurrency(airfare) + "\n" + 
                "Seat Availability = " + seatAvailability;
    }
}
