package com.example.demo.server.invocation;

import com.example.demo.client.ClientRequest;
import com.example.demo.server.ServerReply;
import com.example.demo.server.servant.BookingsImpl;
import com.example.demo.server.servant.InformationImpl;
import com.example.demo.server.servant.MonitoringImpl;
import com.example.demo.server.servant.models.*;
import com.example.demo.utils.InsufficientSeatsException;
import com.example.demo.utils.MarshallUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public abstract class Invocation {
    private BookingsImpl bookings;
    private MonitoringImpl monitoring;
    private InformationImpl information;

    public Invocation(BookingsImpl bookings, MonitoringImpl monitoring, InformationImpl information) {
        this.bookings = bookings;
        this.monitoring = monitoring;
        this.information = information;
    }
    public abstract ServerReply handleRequest(String clientAddress, int clientPort, ClientRequest clientRequest);

    // Call service based on serviceId.
    // Returns a ServerReply object
    protected ServerReply callServant(ClientID clientID, int serviceId, String requestBody) {
        HashMap<String, String> requestBodyParsed = DeconstructAttribute(requestBody);
        switch (serviceId) {
            case 1: {
                List<Information> res = information.GetFlightsBySourceAndDestination(requestBodyParsed.get("src"), requestBodyParsed.get("dst"));
                if (res.isEmpty()) {
                    ServerReply reply = new ServerReply(false, "ERROR message: no flight matches the source and destination places of " + requestBodyParsed.get("src") + " and " + requestBodyParsed.get("dst"), Optional.empty());
                    return reply;
                }
                ServerReply reply = new ServerReply(true, "Flights found: ", Optional.of(res));
                return reply;
            }
            case 2: {
                Optional<Information> res = information.GetFlightById(Integer.parseInt(requestBodyParsed.get("flightID")));
                if (res.isEmpty()) {
                    ServerReply reply = new ServerReply(false, "ERROR message: the flight with the requested identifier " + Integer.parseInt(requestBodyParsed.get("flightID")) + " does not exist", Optional.empty());
                    return reply;
                }
                ServerReply reply = new ServerReply(true, "Details for flight " + Integer.parseInt(requestBodyParsed.get("flightID")) + " is: ", Optional.of(res.get()));
                return reply;
            }
            case 3: {
                Optional<Information> checkIfPresent = information.GetFlightById(Integer.parseInt(requestBodyParsed.get("flightID")));
                if (checkIfPresent.isEmpty()) {
                    ServerReply reply = new ServerReply(false, "ERROR message: the flight with the requested identifier " + Integer.parseInt(requestBodyParsed.get("flightID")) + " does not exist", Optional.empty());
                    return reply;
                }
                try {
                    Optional<Bookings> res = bookings.AddFlightBooking(clientID, Integer.parseInt(requestBodyParsed.get("flightID")), Integer.parseInt(requestBodyParsed.get("numSeats")));
                    if (res.isEmpty()) {
                        ServerReply reply = new ServerReply(false, "ERROR message: you already have an existing booking. Delete that booking or make changes to that booking using the other services.", Optional.empty());
                        return reply;
                    }
                    ServerReply reply = new ServerReply(true, "Reservation made. Here are the details: ", Optional.ofNullable(res.get()));
                    return reply;
                } catch (InsufficientSeatsException e) {
                    ServerReply reply = new ServerReply(false, "ERROR message: insufficient number of available seats for flight " + Integer.parseInt(requestBodyParsed.get("flightID")), Optional.empty());
                    return reply;
                }
            }
            case 4: {
                Optional<Monitoring> res = monitoring.AddToMonitorList(clientID, Integer.parseInt(requestBodyParsed.get("flightID")), Integer.parseInt(requestBodyParsed.get("monitorInterval")));
                if (res.isEmpty()) {
                    ServerReply reply = new ServerReply(false, "ERROR message: the flight with the requested identifier " + Integer.parseInt(requestBodyParsed.get("flightID")) + " does not exist", Optional.empty());
                    return reply;
                }
                ServerReply reply = new ServerReply(true, "You will now monitor flight " + Integer.parseInt(requestBodyParsed.get("flightID")) + " for the next " + Integer.parseInt(requestBodyParsed.get("monitorInterval")) + " seconds.", Optional.of(res.get()));
                return reply;
            }
            case 5: {
                Optional<Bookings> res = bookings.DeleteFlightBooking(clientID, Integer.parseInt(requestBodyParsed.get("flightID")));
                if (res.isEmpty()) {
                    ServerReply reply = new ServerReply(false, "ERROR message: the flight with the requested identifier " + Integer.parseInt(requestBodyParsed.get("flightID")) + " under your name does not exist", Optional.empty());
                    return reply;
                }
                ServerReply reply = new ServerReply(true, "You have deleted your flight booking for flight " + Integer.parseInt(requestBodyParsed.get("flightID")) + ". Your booking was: ", Optional.of(res.get()));
                return reply;
            }
            case 6: {
                try {
                    Optional<Bookings> res = bookings.UpdateFlightBooking(clientID, Integer.parseInt(requestBodyParsed.get("flightID")), Integer.parseInt(requestBodyParsed.get("numSeats")));
                    if (res.isEmpty()) {
                        ServerReply reply = new ServerReply(false, "ERROR message: the flight with the requested identifier " + Integer.parseInt(requestBodyParsed.get("flightID")) + " under your name does not exist", Optional.empty());
                        return reply;
                    }
                    ServerReply reply = new ServerReply(true, "You have increased your flight booking for " + Integer.parseInt(requestBodyParsed.get("flightID")) + " by " + Integer.parseInt(requestBodyParsed.get("numSeats")) + " seats. Your updated booking is: ", Optional.of(res.get()));
                    return reply;
                } catch (InsufficientSeatsException e) {
                    ServerReply reply = new ServerReply(false, "ERROR message: insufficient number of available seats for flight " + Integer.parseInt(requestBodyParsed.get("flightID")), Optional.empty());
                    return reply;
                }
            }

        }
        return null;
    }

    // Parses requestBody from ClientRequest to get required parameters for services.
    private HashMap<String, String> DeconstructAttribute(String str){
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
