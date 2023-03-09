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

public interface FlightMonitoringRepository extends PagingAndSortingRepository<FlightMonitoring, ClientID> {
    // For service 4 - monitor updates made to seat availability information of a flight at the server through callback
    // for a designated time period (monitor interval)

    // Check if clientID and flightID exists in the database
    @Query(value = "SELECT * FROM Flight_Monitoring WHERE client_ip = :clientIP AND client_port = :clientPort AND flight_identifier = :flightID", nativeQuery = true)
    Optional<FlightMonitoring> findFlightMonitoringByClientIDAndFlightID(
            @Param("clientIP") String clientIP,
            @Param("clientPort") int clientPort,
            @Param("flightID") int flightID);

    // Add new record if above does not exist
    @Transactional
    @Modifying
    @Query(value = "INSERT INTO Flight_Monitoring (client_ip, client_port, flight_identifier, monitor_interval) VALUES (:clientIP, :clientPort, :flightID, :monitorInterval)", nativeQuery = true)
    int insertFlightMonitoringByClientIDAndFlightID(
            @Param("clientIP") String clientIP,
            @Param("clientPort") int clientPort,
            @Param("flightID") int flightID,
            @Param("monitorInterval") int monitorInterval);

    // Update monitor interval if above exists
    @Transactional
    @Modifying
    @Query(value = "UPDATE Flight_Monitoring SET monitor_interval = :monitorInterval WHERE client_ip = :clientIP AND client_port = :clientPort AND flight_identifier = :flightID", nativeQuery = true)
    int updateFlightMonitoringByClientIDAndFlightID(
            @Param("clientIP") String clientIP,
            @Param("clientPort") int clientPort,
            @Param("flightID") int flightID,
            @Param("monitorInterval") int monitorInterval);

    // Remove monitoring record (when time is up)
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM Flight_Monitoring WHERE client_ip = :clientIP AND client_port = :clientPort AND flight_identifier = :flightID", nativeQuery = true)
    int deleteFlightMonitoringByClientIDAndFlightID(
            @Param("clientIP") String clientIP,
            @Param("clientPort") int clientPort,
            @Param("flightID") int flightID);

    // Get all records to send updates
    @Query(value = "SELECT * FROM Flight_Monitoring WHERE flight_identifier = :flightID", nativeQuery = true)
    List<FlightMonitoring> findAllFlightMonitoring(@Param("flightID") int flightID);
}