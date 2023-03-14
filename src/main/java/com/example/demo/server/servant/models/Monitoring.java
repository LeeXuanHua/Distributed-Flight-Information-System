package com.example.demo.server.servant.models;

import java.time.LocalDateTime;

import javax.persistence.*;

/**
 * Contains IP address, port, flight identifier, and monitor interval
 */

@Entity(name="Flight_Monitoring")
@Table(name = "flight_monitoring", uniqueConstraints = {
        @UniqueConstraint(name = "uniqueClientID", columnNames = {"client_ip", "client_port"})
})
public class Monitoring {
    @EmbeddedId
    private ClientID clientID;

    @Column(name = "flight_identifier", nullable = false)
    private int flightID;
    @Column(name = "expiry", nullable = false)
    private LocalDateTime expiry;

    public Monitoring(ClientID clientID, int flightID, LocalDateTime expiry) {
        this.clientID = clientID;
        this.flightID = flightID;
        this.expiry = expiry;
    }

    public Monitoring() {}

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

    public LocalDateTime getExpiry() {
        return expiry;
    }

    public void setExpiry(LocalDateTime expiry) {
        this.expiry = expiry;
    }

    @Override
    public String toString() {
        return "FlightMonitoring{" +
                "clientID=" + clientID +
                ", flightID=" + flightID +
                ", monitorInterval=" + expiry +
                '}';
    }
}
