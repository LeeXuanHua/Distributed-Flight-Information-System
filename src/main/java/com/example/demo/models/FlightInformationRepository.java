package com.example.demo.models;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface FlightInformationRepository extends PagingAndSortingRepository<FlightInformation, Integer> {
    // For service 1 - query the flight identifier(s) by specifying the source and destination places
    @Query(
            value = "SELECT * FROM Flight_Information WHERE source = :src AND destination = :dest",
            nativeQuery = true)
    List<FlightInformation> findFlightsBySrcAndDest(
            @Param("src") String src,
            @Param("dest") String dest);

    // For service 2 - query the departure time, airfare and seat availability by specifying the flight identifier
    @Query(value = "SELECT f FROM Flight_Information f WHERE f.flightID = ?1")
    Optional<FlightInformation> findFlightsByFlightID(int flightID);

    // For service 3 - make seat reservation by specifying the flight identifier and the number of seats to reserve
    // Also for service 5 - remove seat reservation record
    // Also for service 6 - increment seat reservation by specified number
    @Transactional
    @Modifying
    @Query("UPDATE Flight_Information u SET u.seatAvailability = :newSeatAvailability WHERE u.flightID = :flightID")
    int  updateFlightsSeatAvailability(
            @Param("flightID") int flightID,
            @Param("newSeatAvailability") int newSeatAvailability);


    // Miscellaneous Services
    @Query(value = "SELECT f FROM Flight_Information f WHERE f.src = ?1")
    Optional<FlightInformation> findFlightsBySrc(String src);

    @Query(value = "SELECT f FROM Flight_Information f WHERE f.dest = ?1")
    Optional<FlightInformation> findFlightsByDest(String dest);

    @Query(value = "SELECT f FROM Flight_Information f WHERE f.departureTime = ?1")
    Optional<FlightInformation> findFlightsByDepartureTime(String departureTime);

    @Query(value = "SELECT f FROM Flight_Information f WHERE f.airfare = ?1")
    Optional<FlightInformation> findFlightsByAirfare(double airfare);

    @Query(value = "SELECT f FROM Flight_Information f WHERE f.seatAvailability = ?1")
    Optional<FlightInformation> findFlightsBySeatAvailability(int seatAvailability);

    @Query(value = "SELECT f FROM Flight_Information f WHERE f.seatAvailability >= ?1")
    Optional<FlightInformation> findFlightsBySeatAvailabilityGreaterOrEqual(int seatAvailability);

}
