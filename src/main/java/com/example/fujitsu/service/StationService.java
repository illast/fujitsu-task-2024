package com.example.fujitsu.service;

import com.example.fujitsu.model.Station;
import com.example.fujitsu.repository.StationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StationService {

    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public List<Station> getStations() {
        return stationRepository.findAll();
    }
}
