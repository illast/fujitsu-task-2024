package com.example.fujitsu.service;

import com.example.fujitsu.dto.StationDto;
import com.example.fujitsu.mapper.StationMapper;
import com.example.fujitsu.model.Station;
import com.example.fujitsu.repository.StationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class StationService {

    private final StationRepository stationRepository;
    private final StationMapper stationMapper;

    public List<StationDto> getStations() {
        List<Station> stations = stationRepository.findAll();
        return stationMapper.toDtoList(stations);
    }

    public void addStation(StationDto stationDto) {
        Station station = stationMapper.toEntity(stationDto);
        stationRepository.save(station);
    }
}
