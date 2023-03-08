package com.example.demo.models;

import javax.persistence.*;

/**
 * Contains IP address, port, flight identifier, and monitor interval
 */

@Entity(name="Flight_Monitoring")
@Table(name = "flight_monitoring", uniqueConstraints = {
        @UniqueConstraint(name = "uniqueClientID", columnNames = {"client_ip", "client_port"})
})
public class FlightMonitoring {
    @EmbeddedId
    private ClientID clientID;

    @Column(name = "flight_identifier", nullable = false)
    private int flightID;
    @Column(name = "monitor_interval", nullable = false)
    private int monitorInterval;

    public FlightMonitoring(ClientID clientID, int flightID, int monitorInterval) {
        this.clientID = clientID;
        this.flightID = flightID;
        this.monitorInterval = monitorInterval;
    }

    public FlightMonitoring() {}

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

    public int getMonitorInterval() {
        return monitorInterval;
    }

    public void setMonitorInterval(int monitorInterval) {
        this.monitorInterval = monitorInterval;
    }

    @Override
    public String toString() {
        return "FlightMonitoring{" +
                "clientID=" + clientID +
                ", flightID=" + flightID +
                ", monitorInterval=" + monitorInterval +
                '}';
    }
}
