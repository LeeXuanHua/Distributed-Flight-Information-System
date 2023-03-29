package com.example.demo.server;

import com.example.demo.server.invocation.InvocationAtLeastOnce;
import com.example.demo.server.invocation.InvocationAtMostOnce;
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
            informationRepository.save(new Information(2, "Singapore", "China",
                    LocalDateTime.of(2022, Month.JANUARY, 1, 1, 1, 1),
                    20.00, 20));

            // Seeding for TESTING: 5-6
            ClientID lol = new ClientID("128.0.0.1", 8888);
            bookingsRepository.save(new Bookings(lol, 1, 1));

            System.out.println("----------- All available flights -----------");
            List<Information> allFlights = informationService.GetAllFlights();
            for (Information flight : allFlights) {
                System.out.println(flight);
            }
        };
    }
}