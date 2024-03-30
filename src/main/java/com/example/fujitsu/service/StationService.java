package com.example.fujitsu.service;

import com.example.fujitsu.dto.StationDto;
import com.example.fujitsu.mapper.StationMapper;
import com.example.fujitsu.model.Station;
import com.example.fujitsu.repository.StationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Service
public class StationService {

    private final StationRepository stationRepository;
    private final StationMapper stationMapper;

    private static final Map<String, Double> RBF_VEHICLE_CITY_PRICE_MAP = new HashMap<>(Map.of(
        "tallinn car", 4.0, "tallinn scooter", 3.5, "tallinn bike", 3.0,
        "tartu car", 3.5, "tartu scooter", 3.0, "tartu bike", 2.5,
        "pärnu car", 3.0, "pärnu scooter", 2.5, "pärnu bike", 2.0
    ));

    private static final Set<String> ATEF_VEHICLES = new HashSet<>(Set.of("Scooter", "Bike"));
    private static final double LOW_TEMP_THRESHOLD = -10;
    private static final double LOW_TEMP_FEE = 1.0;
    private static final double MID_TEMP_THRESHOLD = 0;
    private static final double MID_TEMP_FEE = 0.5;

    private static final Set<String> WSEF_VEHICLES = new HashSet<>(Set.of("Bike"));
    private static final Set<String> WPEF_VEHICLES = new HashSet<>(Set.of("Scooter", "Bike"));

    public List<StationDto> getStations() {
        List<Station> stations = stationRepository.findAll();
        return stationMapper.toDtoList(stations);
    }

    public void addStation(StationDto stationDto) {
        Station station = stationMapper.toEntity(stationDto);
        stationRepository.save(station);
    }

    public double calculateFee(String city, String vehicle) {
        city = city.toLowerCase();
        vehicle = vehicle.toLowerCase();

        double fee = calculateRBF(city, vehicle);

        Station station = stationRepository.findTopByNameContainingIgnoreCaseOrderByIdDesc(city);
        if (ATEF_VEHICLES.contains(vehicle)) {
            fee += calculateATEF(station.getAirTemperature());
        }
        if (WSEF_VEHICLES.contains(vehicle)) {
            fee += calculateWSEF();
        }
        if (WPEF_VEHICLES.contains(vehicle)) {
            fee += calculateWPEF();
        }

        return fee;
    }

    private double calculateRBF(String city, String vehicle) {
        String cityVehicle = city + " " + vehicle;
        if (RBF_VEHICLE_CITY_PRICE_MAP.containsKey(cityVehicle)) {
            return RBF_VEHICLE_CITY_PRICE_MAP.get(cityVehicle);
        }
        return 0;
    }

    private double calculateATEF(double airTemperature) {
        if (airTemperature < LOW_TEMP_THRESHOLD) {
            return LOW_TEMP_FEE;
        }
        else if (airTemperature >= LOW_TEMP_THRESHOLD && airTemperature < MID_TEMP_THRESHOLD) {
            return MID_TEMP_FEE;
        }
        return 0;
    }

    private double calculateWSEF() {
        return 0;
    }

    private double calculateWPEF() {
        return 0;
    }
}
