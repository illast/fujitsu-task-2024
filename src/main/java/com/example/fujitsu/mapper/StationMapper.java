package com.example.fujitsu.mapper;

import com.example.fujitsu.dto.StationDto;
import com.example.fujitsu.model.Station;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StationMapper {

    StationDto toDto(Station station);
    Station toEntity(StationDto stationDto);
    List<StationDto> toDtoList(List<Station> stations);
    List<Station> toEntityList(List<StationDto> stationDtos);
}
