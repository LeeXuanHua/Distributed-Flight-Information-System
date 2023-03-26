package com.example.demo.server;

import com.example.demo.server.servant.models.*;
import com.example.demo.server.repositories.BookingsRepository;
import com.example.demo.server.repositories.InformationRepository;
import com.example.demo.server.repositories.MonitoringRepository;
import com.example.demo.server.servant.BookingsImpl;
import com.example.demo.server.servant.InformationImpl;
import com.example.demo.server.servant.MonitoringImpl;
import com.example.demo.utils.InputValidator;
import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@SpringBootApplication
@Slf4j
public class StartServer {

    public static InvocationAtLeastOnce invocationAtLeastOnce;
    public static InvocationAtMostOnce invocationAtMostOnce;

    public static void main(String[] args) {



        Scanner scanner = new Scanner(System.in);

        // 1. commandLineRunner is first run
        SpringApplication.run(StartServer.class, args);
        log.info("Database seeded with random data.");

        // 2. Let user choose invocation semantics
        String input;
        do {
            System.out.println("\n===============================");
            System.out.print("Please select an invocation semantic: \n " +
                    "1. At-least-once \n " +
                    "2. At-most-once \n" +
                    "Your selection (1/2): ");
            input = scanner.next();
        } while (!InputValidator.isInteger(input, Optional.of(1), Optional.of(2)));

        AppServer s;
        try {
            // 3. Open port and initialise socket
            int PORT = AppServer.PORT;
            DatagramSocket socket = new DatagramSocket(PORT);

            // 4. Inject the chosen invocation semantic as a dependency into Server to facilitate Server's strategy design pattern
            if (Integer.parseInt(input) == 1) {
                s = new AppServer(invocationAtLeastOnce, socket);
                log.info("You have selected: At-least-once invocation semantics");
            } else {
                s = new AppServer(invocationAtMostOnce, socket);
                log.info("You have selected: At-most-once invocation semantics");
            }

            // 5. Pass control over to Server
            log.info("Started server at IP address {} and port number {}", InetAddress.getLocalHost().getHostAddress(), PORT);
            while (true) {
                s.run();
            }

        } catch (UnknownHostException | SocketException e) {
            log.error("Error: " + e.getMessage());
        }
    }

    // 0. Demonstrate the various functions and test the database
    @Bean
    CommandLineRunner commandLineRunner(
            BookingsImpl bookingsService,
            MonitoringImpl monitoringService,
            InformationImpl informationService,
            InformationRepository informationRepository,
            BookingsRepository bookingsRepository,
            MonitoringRepository monitoringRepository) {
        invocationAtLeastOnce = new InvocationAtLeastOnce(bookingsService, monitoringService, informationService);
        invocationAtMostOnce = new InvocationAtMostOnce(bookingsService, monitoringService, informationService);
        return args -> {
            // Seeding for TESTING: 1-4
            informationRepository.save(new Information(1, "Singapore", "China",
                    LocalDateTime.of(2023, Month.JANUARY, 1, 1, 1, 1),
                    10.00, 10));

            // Seeding for TESTING: 5-6
            ClientID lol = new ClientID("12345", 8080);
            bookingsRepository.save(new Bookings(lol, 1, 1));

            // FlightInformationRepository
            // Create a fake record
            Faker faker = new Faker();

            int flightID = faker.number().numberBetween(1, 100);
            String src = faker.country().name();
            String dest = faker.country().name();
            LocalDateTime departureTime = LocalDateTime.now();
            double airfare = faker.number().randomDouble(2, 100, 1000);
            int seatAvailability = faker.number().numberBetween(1, 100);

            Information information = new Information(
                    flightID,
                    src,
                    dest,
                    departureTime,
                    airfare,
                    seatAvailability);

            informationRepository.save(information);

            informationRepository.findById(flightID)
                    .ifPresent(s -> {
                        System.out.println(String.format("findById %d: %s", flightID, s));
                    });

            informationRepository.findFlightsBySrcAndDest(src, dest)
                    .forEach(System.out::println);

            informationRepository.findAll().forEach(System.out::println);

            informationRepository.updateFlightsSeatAvailability(flightID, seatAvailability - 5);

            informationRepository.findFlightsBySrcAndDest(src, dest)
                    .forEach(System.out::println);


            // FlightBookingsRepository
            // Create a fake record
            ClientID clientID = new ClientID((String) faker.internet().ipV4Address(), faker.number().numberBetween(1, 100));
            int clientSeat = faker.number().numberBetween(1, 100);

            ClientID clientID2 = new ClientID((String) faker.internet().ipV4Address(), faker.number().numberBetween(1, 100));
            int clientSeat2 = faker.number().numberBetween(1, 100);

            Bookings bookings = new Bookings(
                    clientID,
                    flightID,
                    clientSeat);

            bookingsRepository.save(bookings);

            bookingsRepository.findFlightBookingsByClientIDAndFlightID(clientID.getIP(), clientID.getPort(), flightID)
                    .ifPresent(s -> {
                        System.out.println(String.format("findFlightBookingsByClientIDAndFlightID %s: %s", clientID, s));
                    });

            bookingsRepository.incrementFlightBookings(clientID.getIP(), clientID.getPort(), flightID, 10);

            bookingsRepository.insertFlightBookings(clientID2.getIP(), clientID2.getPort(), flightID, clientSeat2);

            bookingsRepository.findAll().forEach(System.out::println);


            // flightMonitoringRepository
            // Create a fake record
            // int monitorInterval = faker.number().numberBetween(1, 100);
            // int monitorInterval2 = faker.number().numberBetween(1, 100);

            // FlightMonitoring flightMonitoring = new FlightMonitoring(
            //         clientID,
            //         flightID,
            //         monitorInterval);

            // flightMonitoringRepository.save(flightMonitoring);

            // flightMonitoringRepository.findFlightMonitoringByClientIDAndFlightID(clientID.getIP(), clientID.getPort(), flightID)
            //         .ifPresent(s -> {
            //             System.out.println(String.format("findFlightMonitoringByClientIDAndFlightID %s: %s", clientID, s));
            //         });

            // flightMonitoringRepository.updateFlightMonitoringByClientIDAndFlightID(clientID.getIP(), clientID.getPort(), flightID, monitorInterval2);

            // flightMonitoringRepository.insertFlightMonitoringByClientIDAndFlightID(clientID2.getIP(), clientID2.getPort(), flightID, monitorInterval2);

            // flightMonitoringRepository.findAll().forEach(System.out::println);

            // Add 1 flight via FlightService instead of FlightInformationRepository
            //     int flightID_FS = faker.number().numberBetween(1, 100);
            int clientSeat_FS = faker.number().numberBetween(1, 100);
            ClientID clientID_FS = new ClientID((String) faker.internet().ipV4Address(), faker.number().numberBetween(1, 100));

            System.out.println("----------- All avaiable flights -----------");
            List<Information> allFlights = informationService.GetAllFlights();
            for (Information flight : allFlights) {
                System.out.println(flight);
            }

            System.out.println("----------- All current bookings -----------");
            List<Bookings> allBookings = bookingsService.GetAllFlightBookings();
            for (Bookings booking : allBookings) {
                System.out.println(booking);
            }

            // Service 1
            System.out.println("Service 1");
            List<Information> flights = informationService.GetFlightsBySourceAndDestination(src, dest);
            flights.forEach(System.out::println);

            // Service 2
            System.out.println("Service 2");
            Optional<Information> flight = informationService.GetFlightById(flightID);
            System.out.println(flight);

            // Service 3
            System.out.println("Service 3 - Add Flight Booking");
            System.out.println(String.format("Trying to add %s to flight %d with seat %d", clientID_FS, flightID, clientSeat_FS));
            try {
                System.out.println(bookingsService.AddFlightBooking(clientID_FS, flightID, clientSeat_FS));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            System.out.println(informationService.GetFlightById(flightID));
            System.out.println(bookingsService.GetFlightBooking(clientID_FS, flightID));

            // Service 6
            System.out.println("Service 6 - Update Flight Booking");
            System.out.println(String.format("Trying to update %s in flight %d by increasing num of seats by %d", clientID_FS, flightID, clientSeat_FS));
            System.out.println(bookingsService.UpdateFlightBooking(clientID_FS, flightID, clientSeat_FS));
            System.out.println(informationService.GetFlightById(flightID));
            System.out.println(bookingsService.GetFlightBooking(clientID_FS, flightID));

            // Service 5
            System.out.println("Service 5 - Remove Flight Booking");
            System.out.println(String.format("Trying to remove %s from flight %d", clientID_FS, flightID));
            System.out.println(bookingsService.DeleteFlightBooking(clientID_FS, flightID));
            System.out.println(informationService.GetFlightById(flightID));
            System.out.println(bookingsService.GetFlightBooking(clientID_FS, flightID));

            // Service 4
            System.out.println("Service 4 - Add To Monitoring");
            System.out.println(String.format("Trying to add %s to monitoring list for flight %d", clientID_FS, flightID));
            System.out.println(monitoringService.AddToMonitorList(clientID_FS, flightID, 10));
            System.out.println(monitoringService.GetAllMonitorList());
        };
    }
}