package com.codecool.solarwatch.controller;

import com.codecool.solarwatch.exception.CityNotFoundException;
import com.codecool.solarwatch.model.City;
import com.codecool.solarwatch.model.Coordinates;
import com.codecool.solarwatch.model.SunTimes;
import com.codecool.solarwatch.model.SunTimesResponse;
import com.codecool.solarwatch.service.CityService;
import com.codecool.solarwatch.service.SunTimesService;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SunTimesControllerTest {
    private final SunTimesService sunTimesService = mock(SunTimesService.class);
    private final CityService cityService = mock(CityService.class);
    private final SunTimesController controller = new SunTimesController(sunTimesService);

    @Test
    void testGetSunTimesSuccess() {
        String city = "London";
        String country = "UK";
        LocalDate date = LocalDate.of(2025, 5, 29);
        Coordinates coords = new Coordinates(51.5074, -0.1278);
        City cityObj = new City(city, "", country, coords);
        SunTimes sunTimes = new SunTimes(date, LocalTime.of(5, 52), LocalTime.of(18, 2), cityObj);

        when(sunTimesService.getSunTimes(city, country, date)).thenReturn(sunTimes);

        ResponseEntity<?> response = controller.getSunTimes(city, date, "local", country);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof SunTimesResponse);

        SunTimesResponse body = (SunTimesResponse) response.getBody();
        assertEquals(city, body.city());
        assertEquals(date, body.date());
        assertEquals(sunTimes, body.sunTimes());
        assertEquals("local", body.timezone());
    }

    @Test
    void testGetSunTimesMissingCity() {
        ResponseEntity<?> response = controller.getSunTimes("", LocalDate.now(), "local", "");
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("City parameter is required", response.getBody());
    }

    @Test
    void testGetSunTimesCityNotFound() throws CityNotFoundException {
        String city = "UnknownCity";
        String country = "Nowhere";
        when(sunTimesService.getSunTimes(city, country, LocalDate.now()))
                .thenThrow(new CityNotFoundException("City not found"));

        ResponseEntity<?> response = controller.getSunTimes(city, null, "local", country);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("City not found: " + city, response.getBody());
    }

    @Test
    void testGetSunTimesDefaultDate() {
        String city = "London";
        String country = "UK";
        Coordinates coords = new Coordinates(51.5074, -0.1278);
        City cityObj = new City(city, "", country, coords);
        SunTimes sunTimes = new SunTimes(LocalDate.now(), LocalTime.of(5, 52), LocalTime.of(18, 2), cityObj);

        when(sunTimesService.getSunTimes(eq(city), eq(country), any(LocalDate.class))).thenReturn(sunTimes);

        ResponseEntity<?> response = controller.getSunTimes(city, null, "local", country);

        assertEquals(200, response.getStatusCodeValue());
        SunTimesResponse body = (SunTimesResponse) response.getBody();
        assertEquals(city, body.city());
        assertNotNull(body.date());
        assertEquals(sunTimes, body.sunTimes());
    }
}