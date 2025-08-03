package com.codecool.solarwatch.repository;

import com.codecool.solarwatch.model.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CityRepository extends JpaRepository<City, Long> {

    Optional<City> findByNameAndCountry(String name, String country);

    City findByName(String name);

    List<City> findByCountry(String country);
}
