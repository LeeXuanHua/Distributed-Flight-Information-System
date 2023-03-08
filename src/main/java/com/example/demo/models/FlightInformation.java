package com.example.demo.models;

import javax.persistence.*;

/**
 * Contains flight identifier, source, destination, departure time, airfare, and seat availability
 */
@Entity(name="Flight_Information")
@Table(name="flight_information")
public class FlightInformation {
    @Id
//    @SequenceGenerator(
//            name = "flightID_sequence",
//            sequenceName = "flightID_sequence",
//            initialValue = 1000,
//            allocationSize = 1
//    )
//    @GeneratedValue(
//            strategy = GenerationType.SEQUENCE,
//            generator = "flightID_sequence"
//    )
    @Column(name = "flight_identifier", unique = true, nullable = false)
    private int flightID;
    @Column(name = "source", nullable = false)
    private String src;
    @Column(name = "destination", nullable = false)
    private String dest;
    @Column(name = "departure_time", nullable = false)
    private String departureTime;
    @Column(name = "airfare", nullable = false)
    private double airfare;
    @Column(name = "seat_availability", nullable = false)
    private int seatAvailability;

    public FlightInformation(int flightID, String src, String dest, String departureTime, double airfare, int seatAvailability) {
        this.flightID = flightID;
        this.src = src;
        this.dest = dest;
        this.departureTime = departureTime;
        this.airfare = airfare;
        this.seatAvailability = seatAvailability;
    }

    public FlightInformation() {}

    public int getFlightID() {
        return flightID;
    }

    public void setFlightID(int flightID) {
        this.flightID = flightID;
    }

    public String getsrc() {
        return src;
    }

    public void setsrc(String src) {
        this.src = src;
    }

    public String getdest() {
        return dest;
    }

    public void setdest(String dest) {
        this.dest = dest;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public double getAirfare() {
        return airfare;
    }

    public void setAirfare(double airfare) {
        this.airfare = airfare;
    }

    public int getSeatAvailability() {
        return seatAvailability;
    }

    public void setSeatAvailability(int seatAvailability) {
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
}
