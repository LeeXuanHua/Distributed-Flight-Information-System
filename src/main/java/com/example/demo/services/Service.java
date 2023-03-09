package com.example.demo.services;

import java.util.ArrayList;
import com.example.demo.models.FlightInformation;

public class Service {
    public Service() {

    }

    public ArrayList<FlightInformation> GetFlightsBySourceAndDestination(String src, String dest) {
        ArrayList<FlightInformation> flights = this.GetAllFlights();
        ArrayList<FlightInformation> res = new ArrayList<> ();

        for (FlightInformation flight : flights) {
            if (flight.getsrc() == src && flight.getdest() == dest) res.add(flight);
        }

        return res;
    }

    public FlightInformation GetFlightById(int id) {
        ArrayList<FlightInformation> flights = this.GetAllFlights();

        for (FlightInformation flight : flights) {
            if (flight.getFlightID() == id) return flight;
        }

        return null;
    }

    public boolean AddReservation(int id, int numOfSeats) {
        try {
            //TODO: implement
        } catch (Exception e) {
            System.out.println(e.toString());
            return false;
        }

        return true;
    }

    public boolean DeleteReservation(int id) {
        try {
            //TODO: implement
        } catch (Exception e) {
            System.out.println(e.toString());
            return false;
        }

        return true;
    }

    public boolean UpdateReservation(int id, int numOfSeats) {
        try {
            //TODO: implement
        } catch (Exception e) {
            System.out.println(e.toString());
            return false;
        }

        return true;
    }

    public boolean AddToMonitorList(int id, int interval) {
        try {
            //TODO: implement
        } catch (Exception e) {
            System.out.println(e.toString());
            return false;
        }

        return true;
    }

    private ArrayList<FlightInformation> GetAllFlights() {
        //TODO: Add logic for querying database
        return new ArrayList<FlightInformation>();
    }
}
