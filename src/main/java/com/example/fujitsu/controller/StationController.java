package com.example.fujitsu.controller;

import com.example.fujitsu.model.Station;
import com.example.fujitsu.service.StationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/stations")
public class StationController {

    private final StationService stationService;

    @GetMapping
    public List<Station> getStations() {
        return stationService.getStations();
    }
}
