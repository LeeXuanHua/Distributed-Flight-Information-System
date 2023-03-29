package com.example.demo.server.repositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.server.servant.models.Bookings;
import com.example.demo.server.servant.models.ClientID;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = false)
public interface BookingsRepository extends PagingAndSortingRepository<Bookings, ClientID> {
        // Check for existing booking by clientID and flightID
        @Query(value = "SELECT * FROM Flight_Bookings WHERE client_ip = :clientIP AND client_port = :clientPort AND flight_identifier = :flightID", nativeQuery = true)
        Optional<Bookings> findFlightBookingsByClientIDAndFlightID(
                @Param("clientIP") String clientIP,
                @Param("clientPort") int clientPort,
                @Param("flightID") int flightID);

        // Service 3 - Add new reservation
        @Transactional
        @Modifying
        @Query(value = "INSERT INTO Flight_Bookings (client_ip, client_port, flight_identifier, number_of_reserved_seats) VALUES (:clientIP, :clientPort, :flightID, :seatAvailability)", nativeQuery = true)
        int insertFlightBookings(
                @Param("clientIP") String clientIP,
                @Param("clientPort") int clientPort,
                @Param("flightID") int flightID,
                @Param("seatAvailability") int seatAvailability);

        // For service 5 - remove seat reservation record
        @Transactional
        @Modifying
        @Query(value = "DELETE FROM Flight_Bookings WHERE client_ip = :clientIP AND client_port = :clientPort AND flight_identifier = :flightID", nativeQuery = true)
        int deleteFlightBookings(
                @Param("clientIP") String clientIP,
                @Param("clientPort") int clientPort,
                @Param("flightID") int flightID);

        // For service 6 - increment seat reservation by specified number
        @Transactional
        @Modifying
        @Query(value = "UPDATE Flight_Bookings SET number_of_reserved_seats = number_of_reserved_seats + :seatAvailability WHERE client_ip = :clientIP AND client_port = :clientPort AND flight_identifier = :flightID", nativeQuery = true)
        int incrementFlightBookings(
                @Param("clientIP") String clientIP,
                @Param("clientPort") int clientPort,
                @Param("flightID") int flightID,
                @Param("seatAvailability") int seatAvailability);

        // Get all flight bookings - for debugging
        @Query(value = "SELECT * FROM Flight_Bookings", nativeQuery = true)
        List<Bookings> findAllFlightBookings();
}
