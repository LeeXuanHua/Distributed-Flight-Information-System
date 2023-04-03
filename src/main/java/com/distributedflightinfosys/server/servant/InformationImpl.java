package com.distributedflightinfosys.server.servant;

import com.distributedflightinfosys.server.repositories.InformationRepository;
import com.distributedflightinfosys.server.servant.models.Information;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InformationImpl implements InformationInterface {
    private InformationRepository informationRepository;

    public InformationImpl(@Autowired InformationRepository informationRepository) {
        this.informationRepository = informationRepository;
    }

    //Service 1 - Get Flights By Source and Destination
    @Override
    public List<Information> GetFlightsBySourceAndDestination(String src, String dest) {
        return informationRepository.findFlightsBySrcAndDest(src, dest);
    }

    //Service 2 - Get Flight By Id
    @Override
    public Optional<Information> GetFlightById(int id) {
        return informationRepository.findFlightByFlightID(id);
    }

    @Override
    public List<Information> GetAllFlights() {
        return informationRepository.findAllFlights();
    }
}
