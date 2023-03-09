package com.example.demo;

import com.example.demo.models.*;
import com.example.demo.services.FlightService;
import com.github.javafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Optional;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    // Demonstrate the various functions and test the database
    @Bean
    CommandLineRunner commandLineRunner(
            FlightService flightService,
            FlightInformationRepository flightInformationRepository,
            FlightBookingsRepository flightBookingsRepository,
            FlightMonitoringRepository flightMonitoringRepository) {
        return args -> {
            // FlightInformationRepository
            // Create a fake record
            Faker faker = new Faker();

            int flightID = faker.number().numberBetween(1, 100);
            String src = faker.country().name();
            String dest = faker.country().name();
            String departureTime = faker.name().fullName();
            double airfare = faker.number().randomDouble(2, 100, 1000);
            int seatAvailability = faker.number().numberBetween(1, 100);

            FlightInformation flightInformation = new FlightInformation(
                    flightID,
                    src,
                    dest,
                    departureTime,
                    airfare,
                    seatAvailability);

            flightInformationRepository.save(flightInformation);

            flightInformationRepository.findById(flightID)
                    .ifPresent(s -> {
                        System.out.println(String.format("findById %d: %s", flightID, s));
                    });

            flightInformationRepository.findFlightsBySrcAndDest(src, dest)
                    .forEach(System.out::println);

            flightInformationRepository.findAll().forEach(System.out::println);

            flightInformationRepository.updateFlightsSeatAvailability(flightID, seatAvailability - 5);

            flightInformationRepository.findFlightsBySrcAndDest(src, dest)
                    .forEach(System.out::println);


            // FlightBookingsRepository
            // Create a fake record
            ClientID clientID = new ClientID((String) faker.internet().ipV4Address(), faker.number().numberBetween(1, 100));
            int clientSeat = faker.number().numberBetween(1, 100);

            ClientID clientID2 = new ClientID((String) faker.internet().ipV4Address(), faker.number().numberBetween(1, 100));
            int clientSeat2 = faker.number().numberBetween(1, 100);

            FlightBookings flightBookings = new FlightBookings(
                    clientID,
                    flightID,
                    clientSeat);

            flightBookingsRepository.save(flightBookings);

            flightBookingsRepository.findFlightBookingsByClientIDAndFlightID(clientID.getIP(), clientID.getPort(), flightID)
                    .ifPresent(s -> {
                        System.out.println(String.format("findFlightBookingsByClientIDAndFlightID %s: %s", clientID, s));
                    });

            flightBookingsRepository.incrementFlightBookings(clientID.getIP(), clientID.getPort(), flightID, 10);

            flightBookingsRepository.insertFlightBookings(clientID2.getIP(), clientID2.getPort(), flightID, clientSeat2);

            flightBookingsRepository.findAll().forEach(System.out::println);


            // flightMonitoringRepository
            // Create a fake record
            int monitorInterval = faker.number().numberBetween(1, 100);
            int monitorInterval2 = faker.number().numberBetween(1, 100);

            FlightMonitoring flightMonitoring = new FlightMonitoring(
                    clientID,
                    flightID,
                    monitorInterval);

            flightMonitoringRepository.save(flightMonitoring);

            flightMonitoringRepository.findFlightMonitoringByClientIDAndFlightID(clientID.getIP(), clientID.getPort(), flightID)
                    .ifPresent(s -> {
                        System.out.println(String.format("findFlightMonitoringByClientIDAndFlightID %s: %s", clientID, s));
                    });

            flightMonitoringRepository.updateFlightMonitoringByClientIDAndFlightID(clientID.getIP(), clientID.getPort(), flightID, monitorInterval2);

            flightMonitoringRepository.insertFlightMonitoringByClientIDAndFlightID(clientID2.getIP(), clientID2.getPort(), flightID, monitorInterval2);

            flightMonitoringRepository.findAll().forEach(System.out::println);

            // Add 1 flight via FlightService instead of FlightInformationRepository
        //     int flightID_FS = faker.number().numberBetween(1, 100);
            int clientSeat_FS = faker.number().numberBetween(1, 100);
            ClientID clientID_FS = new ClientID((String) faker.internet().ipV4Address(), faker.number().numberBetween(1, 100));

            System.out.println("----------- All avaiable flights -----------");
            List<FlightInformation> allFlights = flightService.GetAllFlights();
            for (FlightInformation flight : allFlights) {
                System.out.println(flight);
            }

            System.out.println("----------- All current bookings -----------");
            List<FlightBookings> allBookings = flightService.GetAllFlightBookings();
            for (FlightBookings booking : allBookings) {
                System.out.println(booking);
            }

            // Service 1
            System.out.println("Service 1");
            List<FlightInformation> flights = flightService.GetFlightsBySourceAndDestination(src, dest);
            flights.forEach(System.out::println);

            // Service 2
            System.out.println("Service 2");
            Optional<FlightInformation> flight = flightService.GetFlightById(flightID);
            System.out.println(flight);

            // Service 3
            System.out.println("Service 3 - Add Flight Booking");
            System.out.println(String.format("Trying to add %s to flight %d with seat %d", clientID_FS, flightID, clientSeat_FS));
            flightService.AddFlightBooking(clientID_FS.getIP(), clientID_FS.getPort(), flightID, clientSeat_FS);
            System.out.println(flightService.GetFlightById(flightID));
            System.out.println(flightService.GetFlightBooking(clientID_FS.getIP(), clientID_FS.getPort(), flightID));

            // Service 6
            System.out.println("Service 6 - Update Flight Booking");
            System.out.println(String.format("Trying to update %s in flight %d by increasing num of seats by %d", clientID_FS, flightID, clientSeat_FS));
            flightService.UpdateFlightBooking(clientID_FS.getIP(), clientID_FS.getPort(), flightID, clientSeat_FS);
            System.out.println(flightService.GetFlightById(flightID));
            System.out.println(flightService.GetFlightBooking(clientID_FS.getIP(), clientID_FS.getPort(), flightID));

            // Service 5
            System.out.println("Service 5 - Remove Flight Booking");
            System.out.println(String.format("Trying to remove %s from flight %d", clientID_FS, flightID));
            flightService.DeleteFlightBooking(clientID_FS.getIP(), clientID_FS.getPort(), flightID);
            System.out.println(flightService.GetFlightById(flightID));
            System.out.println(flightService.GetFlightBooking(clientID_FS.getIP(), clientID_FS.getPort(), flightID));
        };
    }
}