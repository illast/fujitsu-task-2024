package com.example.fujitsu.repository;

import com.example.fujitsu.model.Station;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StationRepository extends JpaRepository<Station, Integer> {
    Station findTopByNameContainingIgnoreCaseOrderByIdDesc(String name);
}
