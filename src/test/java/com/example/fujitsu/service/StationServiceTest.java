package com.example.fujitsu.service;

import com.example.fujitsu.dto.StationDto;
import com.example.fujitsu.exception.ApplicationException;
import com.example.fujitsu.mapper.StationMapper;
import com.example.fujitsu.mapper.StationMapperImpl;
import com.example.fujitsu.model.Station;
import com.example.fujitsu.repository.StationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.example.fujitsu.service.StationService.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;


@ExtendWith(MockitoExtension.class)
class StationServiceTest {

    @Mock
    private StationRepository stationRepository;

    @Spy
    private StationMapper stationMapper = new StationMapperImpl();

    @InjectMocks
    private StationService stationService;

    @Test
    void testGetStations() {
        List<Station> stations = new ArrayList<>();
        stations.add(Station.builder().name("Tallinn").build());
        stations.add(Station.builder().name("Tartu").build());
        stations.add(Station.builder().name("Pärnu").build());
        when(stationRepository.findAll()).thenReturn(stations);

        List<StationDto> result = stationService.getStations();

        assertEquals(3, result.size());
        assertEquals("Tallinn", result.get(0).getName());
        assertEquals("Tartu", result.get(1).getName());
        assertEquals("Pärnu", result.get(2).getName());
    }

    @Test
    void testAddStation() {
        StationDto stationDto = StationDto.builder().name("Tallinn").build();
        Station station = Station.builder().name("Tallinn").build();

        when(stationMapper.toEntity(stationDto)).thenReturn(station);

        stationService.addStation(stationDto);

        verify(stationRepository).save(station);
    }

    @Test
    void testCalculateFee_Simple_Success() {
        double fee = stationService.calculateFee("Tallinn", "Car");
        assertEquals(4.0, fee);
    }

    @Test
    void testCalculateFee_NoWeatherDataForCity_ExceptionThrown() {
        when(stationRepository.findTopByNameContainingIgnoreCaseOrderByIdDesc("UnknownCity")).thenReturn(null);
        assertThrows(ApplicationException.class, () -> stationService.calculateFee("UnknownCity", "Bike"));
    }

    @Test
    void testCalculateFee_RBFAndATEF_Success() {
        Station station = Station.builder().name("Tallinn").airTemperature(-5.0).windSpeed(0.0).phenomenon("").build();
        when(stationRepository.findTopByNameContainingIgnoreCaseOrderByIdDesc("tallinn")).thenReturn(station);

        double fee = stationService.calculateFee("Tallinn", "Bike");
        assertEquals(RBF_VEHICLE_CITY_PRICE_MAP.get("tallinn bike") + MID_TEMP_FEE, fee);
    }

    @Test
    void testCalculateFee_RBFAndWSEF_Success() {
        Station station = Station.builder().name("Tartu").airTemperature(10.0).windSpeed(15.0).phenomenon("").build();
        when(stationRepository.findTopByNameContainingIgnoreCaseOrderByIdDesc("tartu")).thenReturn(station);

        double fee = stationService.calculateFee("Tartu", "Bike");
        assertEquals(RBF_VEHICLE_CITY_PRICE_MAP.get("tartu bike") + MID_WIND_SPEED_FEE, fee);
    }

    @Test
    void testCalculateFee_RBFAndWPEF_Success() {
        Station station = Station.builder().name("Pärnu").airTemperature(10.0).windSpeed(0.0).phenomenon("Moderate rain").build();
        when(stationRepository.findTopByNameContainingIgnoreCaseOrderByIdDesc("pärnu")).thenReturn(station);

        double fee = stationService.calculateFee("Pärnu", "Scooter");
        assertEquals(RBF_VEHICLE_CITY_PRICE_MAP.get("pärnu scooter") + RAIN_FEE, fee);
    }


    // ATEF

    @Test
    void testCalculateATEF_LowTemperature_Success() {
        double fee = stationService.calculateATEF(-15);
        assertEquals(LOW_TEMP_FEE, fee);
    }

    @Test
    void testCalculateATEF_MidTemperature_Success() {
        double fee = stationService.calculateATEF(-5);
        assertEquals(MID_TEMP_FEE, fee);
    }

    @Test
    void testCalculateATEF_HighTemperature_NoFee() {
        double fee = stationService.calculateATEF(5);
        assertEquals(0, fee);
    }


    // WSEF

    @Test
    void testCalculateWSEF_LowWindSpeed_NoFee() {
        double fee = stationService.calculateWSEF(5);
        assertEquals(0, fee);
    }

    @Test
    void testCalculateWSEF_MidWindSpeed_Success() {
        double fee = stationService.calculateWSEF(15);
        assertEquals(MID_WIND_SPEED_FEE, fee);
    }

    @Test
    void testCalculateWSEF_HighWindSpeed_ExceptionThrown() {
        assertThrows(ApplicationException.class, () -> stationService.calculateWSEF(25));
    }


    // WPEF

    @Test
    void testCalculateWPEF_RainPhenomenon_Success() {
        double fee = stationService.calculateWPEF("Moderate rain");
        assertEquals(RAIN_FEE, fee);
    }

    @Test
    void testCalculateWPEF_SnowPhenomenon_Success() {
        double fee = stationService.calculateWPEF("Moderate snowfall");
        assertEquals(SNOW_FEE, fee);
    }

    @Test
    void testCalculateWPEF_ForbiddenPhenomenon_ExceptionThrown() {
        assertThrows(ApplicationException.class, () -> stationService.calculateWPEF("Hail"));
    }

    @Test
    void testCalculateWPEF_CommonPhenomenon_NoFee() {
        double fee = stationService.calculateWPEF("Cloudy");
        assertEquals(0, fee);
    }
}
