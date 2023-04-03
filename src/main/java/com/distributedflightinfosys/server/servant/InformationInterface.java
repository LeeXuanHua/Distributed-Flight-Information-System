package com.distributedflightinfosys.server.servant;

import java.util.List;
import java.util.Optional;

import com.distributedflightinfosys.server.servant.models.Information;

public interface InformationInterface {
    //Service 1
    List<Information> GetFlightsBySourceAndDestination(String src, String dest);

    //Service 2
    Optional<Information> GetFlightById(int id);

    List<Information> GetAllFlights();
}
