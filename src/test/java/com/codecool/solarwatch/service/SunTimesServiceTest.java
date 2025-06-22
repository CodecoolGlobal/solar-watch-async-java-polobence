package com.codecool.solarwatch.service;

import com.codecool.solarwatch.exception.CityNotFoundException;
import com.codecool.solarwatch.model.City;
import com.codecool.solarwatch.model.Coordinates;
import com.codecool.solarwatch.model.SunTimes;
import com.codecool.solarwatch.repository.SunTimesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class SunTimesServiceTest {

    @Mock
    private RestTemplate restTemplate;
    
    @Mock
    private SunTimesRepository sunTimesRepository;
    
    @Mock
    private CityService cityService;
    
    private SunTimesService sunTimesService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sunTimesService = new SunTimesService(restTemplate, sunTimesRepository, cityService);
    }

    @Test
    void getSunTimes_whenInDatabase_returnsFromDatabase() throws CityNotFoundException {
        // Arrange
        String cityName = "London";
        String country = "UK";
        LocalDate date = LocalDate.of(2025, 5, 29);
        City city = new City(cityName, "", country, new Coordinates(51.5074, -0.1278));
        SunTimes expectedSunTimes = new SunTimes(date, LocalTime.of(5, 52), LocalTime.of(18, 2), city);

        when(cityService.getCity(cityName, country)).thenReturn(city);
        when(sunTimesRepository.findByCityAndDate(city, date)).thenReturn(Optional.of(expectedSunTimes));

        // Act
        SunTimes result = sunTimesService.getSunTimes(cityName, country, date);

        // Assert
        assertNotNull(result);
        assertEquals(expectedSunTimes, result);
        verify(sunTimesRepository).findByCityAndDate(city, date);
        verifyNoMoreInteractions(restTemplate, sunTimesRepository);
    }

    @Test
    void getSunTimes_whenNotInDatabase_fetchesFromApiAndSaves() throws CityNotFoundException {
        // Arrange
        String cityName = "London";
        String country = "UK";
        LocalDate date = LocalDate.of(2025, 5, 29);
        City city = new City(cityName, "", country, new Coordinates(51.5074, -0.1278));
        
        Map<String, String> results = Map.of(
                "sunrise", "2025-05-29T05:52:44+00:00",
                "sunset", "2025-05-29T18:02:11+00:00"
        );

        Map<String, Object> mockResponse = Map.of(
                "status", "OK",
                "results", results
        );

        when(cityService.getCity(cityName, country)).thenReturn(city);
        when(sunTimesRepository.findByCityAndDate(city, date)).thenReturn(Optional.empty());
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(mockResponse);
        when(sunTimesRepository.save(any(SunTimes.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        SunTimes result = sunTimesService.getSunTimes(cityName, country, date);

        // Assert
        assertNotNull(result);
        assertEquals(date, result.getDate());
        assertEquals(LocalTime.of(5, 52, 44), result.getSunrise());
        assertEquals(LocalTime.of(18, 2, 11), result.getSunset());
        verify(sunTimesRepository).save(any(SunTimes.class));
    }

    @Test
    void getSunTimes_whenCityNotFound_throwsException() {
        // Arrange
        String cityName = "NonExistentCity";
        String country = "Nowhere";
        LocalDate date = LocalDate.now();

        when(cityService.getCity(cityName, country))
                .thenThrow(new CityNotFoundException("City not found"));

        // Act & Assert
        assertThrows(CityNotFoundException.class, 
                () -> sunTimesService.getSunTimes(cityName, country, date));
    }

    
    @Test
    void getSunTimes_whenApiReturnsError_throwsException() throws CityNotFoundException {
        // Arrange
        String cityName = "London";
        String country = "UK";
        LocalDate date = LocalDate.now();
        City city = new City(cityName, "", country, new Coordinates(51.5074, -0.1278));

        when(cityService.getCity(cityName, country)).thenReturn(city);
        when(sunTimesRepository.findByCityAndDate(city, date)).thenReturn(Optional.empty());
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> sunTimesService.getSunTimes(cityName, country, date));

        assertEquals("Failed to get sun times from API", exception.getMessage());
    }
}
