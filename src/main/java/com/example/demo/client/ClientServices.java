package com.example.demo.client;

import java.util.Scanner;

import com.example.demo.server.servant.models.ClientRequest;
import com.example.demo.utils.MarshallUtil;

public class ClientServices {
    public static ClientRequest getService(int messageId, String serviceId, Scanner scanner) {
        scanner.nextLine();
        switch (serviceId) {
            case "1":
                return getFlightsBySrcAndDst(messageId, scanner);
            case "2":
                return getFlightsById(messageId, scanner);
            case "3":
                return addBooking(messageId, scanner);
            case "4":
                return monitorFlight(messageId, scanner);
            case "5":
                return deleteBooking(messageId, scanner);
            case "6":
                return updateBooking(messageId, scanner);
            default:
                return null;
        }
    }

    private static ClientRequest getFlightsBySrcAndDst(int messageId, Scanner scanner) {
        System.out.println("You have selected Service 1: Get Flights by Source & Destination");
        System.out.print("Please input your desired flight source: ");
        String src = scanner.nextLine();

        System.out.print("Please input your desired flight destination: ");
        String dst = scanner.nextLine();

        String requestBody = Serialise("src", src) + Serialise("dst", dst);
        ClientRequest clientRequest = new ClientRequest(messageId, 1, requestBody);
        return clientRequest;
    }

    private static ClientRequest getFlightsById(int messageId, Scanner scanner) {
        System.out.println("You have selected Service 2: Get Flights by ID");
        System.out.print("Please input the ID of your desired flight: ");
        String flightID = scanner.nextLine();

        String requestBody = Serialise("flightID", flightID);
        ClientRequest clientRequest = new ClientRequest(messageId, 2, requestBody);
        return clientRequest;
    }

    private static ClientRequest addBooking(int messageId, Scanner scanner) {
        System.out.println("You have selected Service 3: Book seats on a flight");
        System.out.print("Please input the ID of your desired flight: ");
        String flightID = scanner.nextLine();

        System.out.print("Please input the number of seats you would like to book: ");
        String numSeats = scanner.nextLine();

        String requestBody = Serialise("flightID", flightID) + Serialise("numSeats", numSeats);
        ClientRequest clientRequest = new ClientRequest(messageId, 3, requestBody);
        return clientRequest;
    }

    private static ClientRequest monitorFlight(int messageId, Scanner scanner) {
        System.out.println("You have selected Service 4: Monitor flight availability");
        System.out.print("Please input the ID of your desired flight: ");
        String flightID = scanner.nextLine();

        System.out.print("Please input the duration you would like to monitor for (in seconds): ");
        String monitorInterval = scanner.nextLine();

        String requestBody = Serialise("flightID", flightID) + Serialise("monitorInterval", monitorInterval);
        ClientRequest clientRequest = new ClientRequest(messageId, 4, requestBody);
        return clientRequest;
    }

    private static ClientRequest deleteBooking(int messageId, Scanner scanner) {
        System.out.println("You have selected Service 5: Delete flight booking");
        System.out.print("Please input the ID of your desired flight: ");
        String flightID = scanner.nextLine();

        String requestBody = Serialise("flightID", flightID);
        ClientRequest clientRequest = new ClientRequest(messageId, 5, requestBody);
        return clientRequest;
    }

    private static ClientRequest updateBooking(int messageId, Scanner scanner) {
        System.out.println("You have selected Service 6: Update flight booking");
        System.out.print("Please input the ID of your desired flight: ");
        String flightID = scanner.nextLine();

        System.out.print("Please input the number of seats you would like to increase your booking by: ");
        String numSeats = scanner.nextLine();

        String requestBody = Serialise("flightID", flightID) + Serialise("numSeats", numSeats);
        ClientRequest clientRequest = new ClientRequest(messageId, 6, requestBody);
        return clientRequest;
    }

    public static String Serialise(String key, String value) {
        return key + MarshallUtil.KV_PAIR + value + MarshallUtil.DELIMITER;
    }
}
