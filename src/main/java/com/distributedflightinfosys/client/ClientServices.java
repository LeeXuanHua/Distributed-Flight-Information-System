package com.distributedflightinfosys.client;

import java.util.Scanner;

import com.distributedflightinfosys.utils.InputValidator;
import com.distributedflightinfosys.utils.MarshallUtil;

public class ClientServices {
    public static ClientRequest getService(int messageId, String serviceId, Scanner scanner) {
        scanner.nextLine();
        // Get a ClientRequest object, based on user input.
        // Prompts and required inputs will change based on the service requested
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

        String requestBody = ConstructAttribute("src", src) + ConstructAttribute("dst", dst);
        ClientRequest clientRequest = new ClientRequest(messageId, 1, requestBody);
        return clientRequest;
    }

    private static ClientRequest getFlightsById(int messageId, Scanner scanner) {
        System.out.println("You have selected Service 2: Get Flight by ID");
        String flightID = getUserInputInteger(scanner, "Please input the ID of your desired flight: ");

        String requestBody = ConstructAttribute("flightID", flightID);
        ClientRequest clientRequest = new ClientRequest(messageId, 2, requestBody);
        return clientRequest;
    }

    private static ClientRequest addBooking(int messageId, Scanner scanner) {
        System.out.println("You have selected Service 3: Book seats on a flight");
        String flightID = getUserInputInteger(scanner, "Please input the ID of your desired flight: ");
        String numSeats = getUserInputInteger(scanner, "Please input the number of seats you would like to book: ");

        String requestBody = ConstructAttribute("flightID", flightID) + ConstructAttribute("numSeats", numSeats);
        ClientRequest clientRequest = new ClientRequest(messageId, 3, requestBody);
        return clientRequest;
    }

    private static ClientRequest monitorFlight(int messageId, Scanner scanner) {
        System.out.println("You have selected Service 4: Monitor flight availability");
        String flightID = getUserInputInteger(scanner, "Please input the ID of your desired flight: ");
        String monitorInterval = getUserInputInteger(scanner, "Please input the duration you would like to monitor for (in seconds): ");

        String requestBody = ConstructAttribute("flightID", flightID) + ConstructAttribute("monitorInterval", monitorInterval);
        ClientRequest clientRequest = new ClientRequest(messageId, 4, requestBody);
        return clientRequest;
    }

    private static ClientRequest deleteBooking(int messageId, Scanner scanner) {
        System.out.println("You have selected Service 5: Delete flight booking");
        String flightID = getUserInputInteger(scanner, "Please input the ID of your desired flight: ");

        String requestBody = ConstructAttribute("flightID", flightID);
        ClientRequest clientRequest = new ClientRequest(messageId, 5, requestBody);
        return clientRequest;
    }

    private static ClientRequest updateBooking(int messageId, Scanner scanner) {
        System.out.println("You have selected Service 6: Update flight booking");
        String flightID = getUserInputInteger(scanner, "Please input the ID of your desired flight: ");
        String numSeats = getUserInputInteger(scanner, "Please input the number of seats you would like to increase your booking by: ");

        String requestBody = ConstructAttribute("flightID", flightID) + ConstructAttribute("numSeats", numSeats);
        ClientRequest clientRequest = new ClientRequest(messageId, 6, requestBody);
        return clientRequest;
    }

    private static String getUserInputInteger(Scanner scanner, String message) {
        String input;
        do {
            System.out.print(message);
            input = scanner.nextLine();
        } while (!InputValidator.isInteger(input));
        return input;
    }

    // Convert a key-value pair to a string, to be used as part of requestBody 
    public static String ConstructAttribute(String key, String value) {
        return key + MarshallUtil.KV_PAIR + value + MarshallUtil.DELIMITER;
    }
}
