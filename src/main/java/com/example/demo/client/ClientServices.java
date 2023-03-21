package com.example.demo.client;

import java.util.Scanner;

// TODO: clarify with xh unmarshalled data format
public class ClientServices {
    public static String getService(String serviceId, Scanner scanner) {
        switch (serviceId) {
            case "1":
                return getFlightsBySrcAndDst(scanner);
            case "2":
                return getFlightsById(scanner);
            case "3":
                return addBooking(scanner);
            case "4":
                return monitorFlight(scanner);
            case "5":
                return deleteBooking(scanner);
            case "6":
                return updateBooking(scanner);
            default:
                return "Invalid Service Id";
        }
    }

    private static String getFlightsBySrcAndDst(Scanner scanner) {
        System.out.println("You have selected Service 1: Get Flights by Source & Destination");
        System.out.print("Please input your desired flight source: ");
        String src = scanner.next();

        System.out.print("Please input your desired flight destination: ");
        String dst = scanner.next();

        return KeyValueToString("src", src) + KeyValueToString("dst", dst);
    }

    private static String getFlightsById(Scanner scanner) {
        System.out.println("You have selected Service 2: Get Flights by ID");
        System.out.print("Please input the ID of your desired flight: ");
        String flightId = scanner.next();

        return KeyValueToString("flightId", flightId);
    }

    private static String addBooking(Scanner scanner) {
        System.out.println("You have selected Service 3: Book seats on a flight");
        System.out.print("Please input the ID of your desired flight: ");
        String flightId = scanner.next();

        System.out.print("Please input the number of seats you would like to book: ");
        String numSeats = scanner.next();

        return KeyValueToString("flightId", flightId) + KeyValueToString("numSeats", numSeats);
    }

    private static String monitorFlight(Scanner scanner) {
        System.out.println("You have selected Service 4: Monitor flight availability");
        System.out.print("Please input the ID of your desired flight: ");
        String flightId = scanner.next();

        System.out.print("Please input the duration you would like to monitor for (in seconds): ");
        String monitorDuration = scanner.next();

        return KeyValueToString("flightId", flightId) + KeyValueToString("monitorDuration", monitorDuration);
    }

    private static String deleteBooking(Scanner scanner) {
        System.out.println("You have selected Service 5: Delete flight booking");
        System.out.print("Please input the ID of your desired flight: ");
        String flightId = scanner.next();

        return KeyValueToString("flightId", flightId);
    }

    private static String updateBooking(Scanner scanner) {
        System.out.println("You have selected Service 6: Update flight booking");
        System.out.print("Please input the ID of your desired flight: ");
        String flightId = scanner.next();

        System.out.print("Please input the number of seats you would like to increase your booking by: ");
        String numSeats = scanner.next();

        return KeyValueToString("flightId", flightId) + KeyValueToString("numSeats", numSeats);
    }

    private static String KeyValueToString(String key, String value) {
        return key + " | " + value + " | ";
    }
}
