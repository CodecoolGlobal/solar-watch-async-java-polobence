package com.codecool.solarwatch.controller;

import com.codecool.solarwatch.exception.CityNotFoundException;
import com.codecool.solarwatch.model.SunTimes;
import com.codecool.solarwatch.model.SunTimesResponse;
import com.codecool.solarwatch.service.SunTimesService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/suntimes")
public class SunTimesController {

    private final SunTimesService sunTimesService;

    public SunTimesController(SunTimesService sunTimesService) {
        this.sunTimesService = sunTimesService;
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<SunTimes>> getAllSunTimes() {
        return ResponseEntity.ok(sunTimesService.getAllSunTimes());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getSunTimesById(@PathVariable Long id) {
        try {
            SunTimes sunTimes = sunTimesService.getSunTimesById(id);
            return ResponseEntity.ok(sunTimes);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createSunTimes(@RequestBody SunTimes sunTimes) {
        try {
            SunTimes createdSunTimes = sunTimesService.createSunTimes(sunTimes);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSunTimes);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateSunTimes(
            @PathVariable Long id,
            @RequestBody SunTimes sunTimesDetails) {
        try {
            SunTimes updatedSunTimes = sunTimesService.updateSunTimes(id, sunTimesDetails);
            return ResponseEntity.ok(updatedSunTimes);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteSunTimes(@PathVariable Long id) {
        try {
            sunTimesService.deleteSunTimes(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('USER')")
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
