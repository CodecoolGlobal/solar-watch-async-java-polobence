package com.codecool.solarwatch.repository;

import com.codecool.solarwatch.model.City;
import com.codecool.solarwatch.model.SunTimes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SunTimesRepository extends JpaRepository<SunTimes, Long> {

    Optional<SunTimes> findByCityAndDate(City city, LocalDate date);


    List<SunTimes> findByCityOrderByDateDesc(City city);

    List<SunTimes> findByDate(LocalDate date);
}
