package com.distributedflightinfosys.server.servant.models;

import java.time.LocalDateTime;

import javax.persistence.*;

import com.distributedflightinfosys.utils.StringHelper;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Contains IP address, port, flight identifier, and monitor interval
 */

@Entity(name="Flight_Monitoring")
@Table(name = "flight_monitoring", uniqueConstraints = {
        @UniqueConstraint(name = "uniqueClientID", columnNames = {"client_ip", "client_port"})
})
@NoArgsConstructor
@Getter
@Setter
public class Monitoring implements DataEntity {
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

    @Override
    public String toString() {
        return "FlightMonitoring{" +
                "clientID=" + clientID +
                ", flightID=" + flightID +
                ", monitorInterval=" + expiry +
                '}';
    }

    @Override
    public String getClientDisplay() {
        return  "Client ID = " + clientID + "\n" +
                "Flight ID = " + flightID + "\n" +
                "Expiry Time = " + StringHelper.formatLocalDateTime(expiry);
    }
}
