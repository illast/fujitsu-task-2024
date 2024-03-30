package com.example.fujitsu.controller;

import com.example.fujitsu.dto.StationDto;
import com.example.fujitsu.service.StationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class StationController {

    private final StationService stationService;

    @GetMapping("/stations")
    public List<StationDto> getStations() {
        return stationService.getStations();
    }

    @GetMapping("/fee")
    public double calculateFee(
            @RequestParam(defaultValue = "tallinn") String city,
            @RequestParam(defaultValue = "car") String vehicle) {
        return stationService.calculateFee(city, vehicle);
    }
}
