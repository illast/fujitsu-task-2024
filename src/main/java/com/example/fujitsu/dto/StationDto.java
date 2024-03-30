package com.example.fujitsu.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class StationDto {

    private Integer id;
    private String name;
    private Integer wmoCode;
    private Double airTemperature;
    private Double windSpeed;
    private String phenomenon;
    private LocalDateTime timestamp;
}
