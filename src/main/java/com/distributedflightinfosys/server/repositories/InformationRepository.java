package com.distributedflightinfosys.server.repositories;

import com.distributedflightinfosys.server.servant.models.Information;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = false)
public interface InformationRepository extends PagingAndSortingRepository<Information, Integer> {
        // For service 1 - query the flight identifier(s) by specifying the source and destination places
        @Query(value = "SELECT * FROM Flight_Information WHERE source = :src AND destination = :dest", nativeQuery = true)
        List<Information> findFlightsBySrcAndDest(
                        @Param("src") String src,
                        @Param("dest") String dest);

        // For service 2 - query the departure time, airfare and seat availability by specifying the flight identifier
        @Query(value = "SELECT f FROM Flight_Information f WHERE f.flightID = ?1")
        Optional<Information> findFlightByFlightID(int flightID);

        // For service 3 - make seat reservation by specifying the flight identifier and
        // the number of seats to reserve
        // Also for service 5 - remove seat reservation record
        // Also for service 6 - increment seat reservation by specified number
        @Transactional
        @Modifying
        @Query("UPDATE Flight_Information u SET u.seatAvailability = :newSeatAvailability WHERE u.flightID = :flightID")
        int updateFlightsSeatAvailability(
                        @Param("flightID") int flightID,
                        @Param("newSeatAvailability") int newSeatAvailability);

        // Miscellaneous Services
        @Query(value = "SELECT f FROM Flight_Information f")
        List<Information> findAllFlights();
    
        @Query(value = "SELECT f FROM Flight_Information f WHERE f.src = ?1")
        Optional<Information> findFlightsBySrc(String src);

        @Query(value = "SELECT f FROM Flight_Information f WHERE f.dest = ?1")
        Optional<Information> findFlightsByDest(String dest);

        @Query(value = "SELECT f FROM Flight_Information f WHERE f.departureTime = ?1")
        Optional<Information> findFlightsByDepartureTime(String departureTime);

        @Query(value = "SELECT f FROM Flight_Information f WHERE f.airfare = ?1")
        Optional<Information> findFlightsByAirfare(double airfare);

        @Query(value = "SELECT f FROM Flight_Information f WHERE f.seatAvailability = ?1")
        Optional<Information> findFlightsBySeatAvailability(int seatAvailability);

        @Query(value = "SELECT f FROM Flight_Information f WHERE f.seatAvailability >= ?1")
        Optional<Information> findFlightsBySeatAvailabilityGreaterOrEqual(int seatAvailability);

}
