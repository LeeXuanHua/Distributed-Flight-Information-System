package com.example.demo.server.interfaces;

import com.example.demo.server.impl.servant.models.Information;

import java.util.List;
import java.util.Optional;

public interface InformationRemoteInterface {
    //Service 1
    List<Information> GetFlightsBySourceAndDestination(String src, String dest);

    //Service 2
    Optional<Information> GetFlightById(int id);

    List<Information> GetAllFlights();
}
