package com.example.fujitsu.service;

import com.example.fujitsu.dto.StationDto;
import com.example.fujitsu.mapper.StationMapper;
import com.example.fujitsu.model.Station;
import com.example.fujitsu.repository.StationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class StationService {

    private final StationRepository stationRepository;
    private final StationMapper stationMapper;

    private static final HashMap<String, Double> CITY_VEHICLE_PRICE_MAP = new HashMap<>(Map.of(
        "tallinn car", 4.0, "tallinn scooter", 3.5, "tallinn bike", 3.0,
        "tartu car", 3.5, "tartu scooter", 3.0, "tartu bike", 2.5,
        "pärnu car", 3.0, "pärnu scooter", 2.5, "pärnu bike", 2.0
    ));

    public List<StationDto> getStations() {
        List<Station> stations = stationRepository.findAll();
        return stationMapper.toDtoList(stations);
    }

    public void addStation(StationDto stationDto) {
        Station station = stationMapper.toEntity(stationDto);
        stationRepository.save(station);
    }

    public double calculateFee(String city, String vehicle) {
        double fee = calculateRBF(city, vehicle);
        fee += calculateATEF();
        fee += calculateWSEF();
        fee += calculateWPEF();
        return fee;
    }

    private double calculateRBF(String city, String vehicle) {
        String cityVehicle = (city + " " + vehicle).toLowerCase();
        if (CITY_VEHICLE_PRICE_MAP.containsKey(cityVehicle)) {
            return CITY_VEHICLE_PRICE_MAP.get(cityVehicle);
        }
        return 0;
    }

    private double calculateATEF() {
        return 0;
    }

    private double calculateWSEF() {
        return 0;
    }

    private double calculateWPEF() {
        return 0;
    }
}
