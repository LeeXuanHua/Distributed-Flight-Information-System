package com.distributedflightinfosys.server.servant.models;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

/**
 * Contains IP address, port to identify a unique client instance
 */
@NoArgsConstructor
@Getter
@Setter
@Embeddable
public class ClientID implements Serializable {
    @Column(name = "client_ip", nullable = false)
    private String IP;

    @Column(name = "client_port", nullable = false)
    private int port;

    public ClientID(String IP, int port) {
        this.IP = IP;
        this.port = port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientID clientID = (ClientID) o;

        if (port != clientID.port) return false;
        return Objects.equals(IP, clientID.IP);
    }

    @Override
    public int hashCode() {
        int result = IP != null ? IP.hashCode() : 0;
        result = 31 * result + port;
        return result;
    }

    @Override
    public String toString() {
        return "ClientID{" +
                "IP='" + IP + '\'' +
                ", Port=" + port +
                '}';
    }
}