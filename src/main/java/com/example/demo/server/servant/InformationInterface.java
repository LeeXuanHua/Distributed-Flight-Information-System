package com.example.demo.server.servant;

import com.example.demo.server.servant.models.Information;

import java.util.List;
import java.util.Optional;

public interface InformationInterface {
    //Service 1
    List<Information> GetFlightsBySourceAndDestination(String src, String dest);

    //Service 2
    Optional<Information> GetFlightById(int id);

    List<Information> GetAllFlights();
}
