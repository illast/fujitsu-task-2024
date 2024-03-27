package com.example.fujitsu.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "station")
public class Station {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private Integer wmoCode;
    private Double airTemperature;
    private Double windSpeed;
    private String phenomenon;
    private LocalDateTime timestamp;
}
