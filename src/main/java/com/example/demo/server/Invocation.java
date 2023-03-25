package com.example.demo.server;

import com.example.demo.server.servant.BookingsImpl;
import com.example.demo.server.servant.InformationImpl;
import com.example.demo.server.servant.MonitoringImpl;
import com.example.demo.server.servant.models.*;
import com.example.demo.utils.InsufficientSeatsException;
import com.example.demo.utils.MarshallUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public abstract class Invocation {
    private BookingsImpl bookings;
    private MonitoringImpl monitoring;
    private InformationImpl information;

    public Invocation(BookingsImpl bookings, MonitoringImpl monitoring, InformationImpl information) {
        this.bookings = bookings;
        this.monitoring = monitoring;
        this.information = information;
    }
    public abstract Object handleRequest(String clientAddress, int clientPort, ClientRequest clientRequest);

    protected Object callServant(ClientID clientID, int serviceId, String requestBody) {
        HashMap<String, String> requestBodyParsed = Deserialise(requestBody);
        log.debug("2. parsed clientReq " + requestBodyParsed);
        switch (serviceId) {
            case 1: {
                List<Information> res = information.GetFlightsBySourceAndDestination(requestBodyParsed.get("src"), requestBodyParsed.get("dst"));
                System.out.println("res " + res);
                return res;
            }
            case 2: {
                Optional<Information> res = information.GetFlightById(Integer.parseInt(requestBodyParsed.get("flightID")));
                if (res.isEmpty()) {
                    return "ERROR message: the flight with the requested identifier does not exist";
                }
                return res.get();
            }
            case 3: {
                Optional<Information> checkIfPresent = information.GetFlightById(Integer.parseInt(requestBodyParsed.get("flightID")));
                if (checkIfPresent.isEmpty()) {
                    return "ERROR message: the flight with the requested identifier does not exist";
                }
                try {
                    Optional<Bookings> res = bookings.AddFlightBooking(clientID, Integer.parseInt(requestBodyParsed.get("flightID")), Integer.parseInt(requestBodyParsed.get("numSeats")));
                    return res.get();
                } catch (InsufficientSeatsException e) {
                    return "ERROR message: insufficient number of available seats";
                }
            }
            case 4: {
                Optional<Monitoring> res = monitoring.AddToMonitorList(clientID, Integer.parseInt(requestBodyParsed.get("flightID")), Integer.parseInt(requestBodyParsed.get("monitorInterval")));
                if (res.isEmpty()) {
                    return "ERROR message: the flight with the requested identifier does not exist";
                }
                return res.get();
            }
            case 5: {
                Optional<Bookings> res = bookings.DeleteFlightBooking(clientID, Integer.parseInt(requestBodyParsed.get("flightID")));
                if (res.isEmpty()) {
                    return "ERROR message: booking with the requested identifier under your name does not exist";
                }
                return res.get();
            }
            case 6: {
                Optional<Bookings> res = bookings.UpdateFlightBooking(clientID, Integer.parseInt(requestBodyParsed.get("flightID")), Integer.parseInt(requestBodyParsed.get("numSeats")));
                if (res.isEmpty()) {
                    return "ERROR message: booking with the requested identifier under your name does not exist";
                }
                return res.get();
            }

        }
        return null;
    }

    private HashMap<String, String> Deserialise(String str){
        int l = 0;
        String key = null;
        HashMap<String, String> res = new HashMap<>();
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == MarshallUtil.KV_PAIR) {
                key = str.substring(l, i);
                l = i+1;
                continue;
            }
            if (str.charAt(i) == MarshallUtil.DELIMITER && (key != null)) {
                res.put(key, str.substring(l, i));
                key = null;
                l = i+1;
            }
        }
        return res;
    }
}
