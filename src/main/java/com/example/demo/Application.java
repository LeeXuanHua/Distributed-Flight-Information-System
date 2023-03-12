package com.example.demo;

import com.example.demo.server.impl.servant.models.*;
import com.example.demo.server.impl.servant.BookingsImpl;
import com.example.demo.server.impl.servant.InformationImpl;
import com.example.demo.server.impl.servant.MonitoringImpl;
import com.example.demo.server.impl.servant.models.repository.BookingsRepository;
import com.example.demo.server.impl.servant.models.repository.InformationRepository;
import com.example.demo.server.impl.servant.models.repository.MonitoringRepository;
import com.github.javafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
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
            BookingsImpl bookingsService,
            MonitoringImpl monitoringService,
            InformationImpl informationService,
            InformationRepository informationRepository,
            BookingsRepository bookingsRepository,
            MonitoringRepository monitoringRepository) {
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
            bookingsService.AddFlightBooking(clientID_FS.getIP(), clientID_FS.getPort(), flightID, clientSeat_FS);
            System.out.println(informationService.GetFlightById(flightID));
            System.out.println(bookingsService.GetFlightBooking(clientID_FS.getIP(), clientID_FS.getPort(), flightID));

            // Service 6
            System.out.println("Service 6 - Update Flight Booking");
            System.out.println(String.format("Trying to update %s in flight %d by increasing num of seats by %d", clientID_FS, flightID, clientSeat_FS));
            bookingsService.UpdateFlightBooking(clientID_FS.getIP(), clientID_FS.getPort(), flightID, clientSeat_FS);
            System.out.println(informationService.GetFlightById(flightID));
            System.out.println(bookingsService.GetFlightBooking(clientID_FS.getIP(), clientID_FS.getPort(), flightID));

            // Service 5
            System.out.println("Service 5 - Remove Flight Booking");
            System.out.println(String.format("Trying to remove %s from flight %d", clientID_FS, flightID));
            bookingsService.DeleteFlightBooking(clientID_FS.getIP(), clientID_FS.getPort(), flightID);
            System.out.println(informationService.GetFlightById(flightID));
            System.out.println(bookingsService.GetFlightBooking(clientID_FS.getIP(), clientID_FS.getPort(), flightID));

            // Service 4
            System.out.println("Service 4 - Add To Monitoring");
            System.out.println(String.format("Trying to add %s to monitoring list for flight %d", clientID_FS, flightID));
            monitoringService.AddToMonitorList(clientID_FS.getIP(), clientID_FS.getPort(), flightID, LocalDateTime.of(2023, 3, 30, 12, 0, 0));
            System.out.println(monitoringService.GetMonitorList());
        };
    }
}