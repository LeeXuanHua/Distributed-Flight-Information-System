package com.example.demo.server.servant;

import com.example.demo.server.servant.models.Information;
import com.example.demo.server.servant.models.InformationRepository;
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

    //Service 1
    @Override
    public List<Information> GetFlightsBySourceAndDestination(String src, String dest) {
        return informationRepository.findFlightsBySrcAndDest(src, dest);
    }

    //Service 2
    @Override
    public Optional<Information> GetFlightById(int id) {
        return informationRepository.findFlightsByFlightID(id);
    }

    @Override
    public List<Information> GetAllFlights() {
        return informationRepository.findAllFlights();
    }
}
