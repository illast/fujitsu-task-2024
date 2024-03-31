package com.example.fujitsu.service;

import com.example.fujitsu.dto.StationDto;
import com.example.fujitsu.exception.ApplicationException;
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

    protected static final Map<String, Double> RBF_VEHICLE_CITY_PRICE_MAP = new HashMap<>(Map.of(
        "tallinn car", 4.0, "tallinn scooter", 3.5, "tallinn bike", 3.0,
        "tartu car", 3.5, "tartu scooter", 3.0, "tartu bike", 2.5,
        "pärnu car", 3.0, "pärnu scooter", 2.5, "pärnu bike", 2.0
    ));

    protected static final Set<String> ATEF_VEHICLES = new HashSet<>(Set.of("scooter", "bike"));
    protected static final double LOW_TEMP_FEE = 1.0;
    protected static final double MID_TEMP_FEE = 0.5;
    protected static final double LOW_TEMP_THRESHOLD = -10;
    protected static final double MID_TEMP_THRESHOLD = 0;

    protected static final Set<String> WSEF_VEHICLES = new HashSet<>(Set.of("bike"));
    protected static final double MID_WIND_SPEED_FEE = 0.5;
    protected static final double MID_WIND_SPEED_THRESHOLD = 10;
    protected static final double HIGH_WIND_SPEED_THRESHOLD = 20;

    protected static final Set<String> WPEF_VEHICLES = new HashSet<>(Set.of("scooter", "bike"));
    protected static final double RAIN_FEE = 1.0;
    protected static final double SNOW_FEE = 0.5;
    protected static final Set<String> RAIN_PHENOMENONS = new HashSet<>(Set.of(
            "Light shower", "Moderate shower", "Heavy shower", "Light rain", "Moderate rain", "Heavy rain"
    ));
    protected static final Set<String> SNOW_PHENOMENONS = new HashSet<>(Set.of(
        "Light snow shower", "Moderate snow shower", "Heavy snow shower", "Light sleet", "Moderate sleet",
        "Light snowfall", "Moderate snowfall", "Heavy snowfall", "Blowing snow", "Drifting snow"
    ));
    protected static final Set<String> FORBIDDEN_PHENOMENONS = new HashSet<>(Set.of(
            "Glaze", "Hail", "Thunder", "Thunderstorm"
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
        city = city.toLowerCase();
        vehicle = vehicle.toLowerCase();

        double fee = calculateRBF(city, vehicle);

        if (ATEF_VEHICLES.contains(vehicle) || WSEF_VEHICLES.contains(vehicle) || WPEF_VEHICLES.contains(vehicle)) {
            try {
                Station station = stationRepository.findTopByNameContainingIgnoreCaseOrderByIdDesc(city);
                if (ATEF_VEHICLES.contains(vehicle)) {
                    fee += calculateATEF(station.getAirTemperature());
                }
                if (WSEF_VEHICLES.contains(vehicle)) {
                    fee += calculateWSEF(station.getWindSpeed());
                }
                if (WPEF_VEHICLES.contains(vehicle)) {
                    fee += calculateWPEF(station.getPhenomenon());
                }
            } catch (Exception e) {
                throw new ApplicationException("No weather data available for this city");
            }
        }
        return fee;
    }

    protected double calculateRBF(String city, String vehicle) {
        String cityVehicle = city + " " + vehicle;
        if (RBF_VEHICLE_CITY_PRICE_MAP.containsKey(cityVehicle)) {
            return RBF_VEHICLE_CITY_PRICE_MAP.get(cityVehicle);
        }
        return 0;
    }

    protected double calculateATEF(double airTemperature) {
        if (airTemperature < LOW_TEMP_THRESHOLD) {
            return LOW_TEMP_FEE;
        }
        else if (airTemperature >= LOW_TEMP_THRESHOLD && airTemperature < MID_TEMP_THRESHOLD) {
            return MID_TEMP_FEE;
        }
        return 0;
    }

    protected double calculateWSEF(double windSpeed) {
        if (windSpeed > MID_WIND_SPEED_THRESHOLD && windSpeed <= HIGH_WIND_SPEED_THRESHOLD) {
            return MID_WIND_SPEED_FEE;
        }
        else if (windSpeed > HIGH_WIND_SPEED_THRESHOLD) {
            throw new ApplicationException("Usage of selected vehicle type is forbidden");
        }
        return 0;
    }

    protected double calculateWPEF(String phenomenon) {
        if (RAIN_PHENOMENONS.contains(phenomenon)) {
            return RAIN_FEE;
        }
        else if (SNOW_PHENOMENONS.contains(phenomenon)) {
            return SNOW_FEE;
        }
        else if (FORBIDDEN_PHENOMENONS.contains(phenomenon)) {
            throw new ApplicationException("Usage of selected vehicle type is forbidden");
        }
        return 0;
    }
}
