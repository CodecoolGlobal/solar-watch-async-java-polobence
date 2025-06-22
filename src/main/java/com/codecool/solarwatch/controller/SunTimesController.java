package com.codecool.solarwatch.controller;

import com.codecool.solarwatch.exception.CityNotFoundException;
import com.codecool.solarwatch.model.SunTimes;
import com.codecool.solarwatch.model.SunTimesResponse;
import com.codecool.solarwatch.service.SunTimesService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api")
public class SunTimesController {

    private final SunTimesService sunTimesService;

    public SunTimesController(SunTimesService sunTimesService) {
        this.sunTimesService = sunTimesService;
    }

    @GetMapping("/sun-times")
    public ResponseEntity<?> getSunTimes(
            @RequestParam String city,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "local") String timezone,
            @RequestParam(defaultValue = "") String country) {

        if (city == null || city.isBlank()) {
            return ResponseEntity.badRequest().body("City parameter is required");
        }

        if (date == null) {
            date = LocalDate.now();
        }

        try {
            SunTimes sunTimes = sunTimesService.getSunTimes(city, country, date);
            SunTimesResponse response = new SunTimesResponse(city, date, sunTimes, timezone);
            return ResponseEntity.ok(response);
        } catch (CityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("City not found: " + city);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error getting sun times: " + e.getMessage());
        }
    }
}
