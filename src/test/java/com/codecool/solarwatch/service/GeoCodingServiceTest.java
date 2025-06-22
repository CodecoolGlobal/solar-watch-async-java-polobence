package com.codecool.solarwatch.service;

import com.codecool.solarwatch.exception.CityNotFoundException;
import com.codecool.solarwatch.model.City;
import com.codecool.solarwatch.model.Coordinates;
import com.codecool.solarwatch.model.GeoCodingResponse;
import com.codecool.solarwatch.repository.CityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GeoCodingServiceTest {

    @Mock
    private RestTemplate restTemplate;
    
    @Mock
    private CityRepository cityRepository;
    
    private GeoCodingService geoCodingService;

    @BeforeEach
    void setUp() {
        geoCodingService = new GeoCodingService(restTemplate, cityRepository);
        ReflectionTestUtils.setField(geoCodingService, "apiKey", "test-api-key");
    }

    @Test
    void getCity_whenCityInDatabase_returnsCity() {
        String cityName = "London";
        String country = "UK";
        City city = new City(cityName, "", country, new Coordinates(51.5074, -0.1278));
        when(cityRepository.findByNameAndCountry(cityName, country))
                .thenReturn(Optional.of(city));

        City result = geoCodingService.getCity(cityName, country);

        assertEquals(city, result);
        verify(cityRepository).findByNameAndCountry(cityName, country);
        verifyNoInteractions(restTemplate);
    }

    @Test
    void getCity_whenCityNotInDatabase_fetchesFromApiAndSaves() {
        String cityName = "London";
        String country = "UK";
        String state = "England";
        double lat = 51.5074;
        double lon = -0.1278;
        
        GeoCodingResponse[] response = {new GeoCodingResponse(cityName, lat, lon, country, state)};
        
        when(cityRepository.findByNameAndCountry(cityName, country)).thenReturn(Optional.empty());
        when(cityRepository.findByName(cityName)).thenReturn(null);
        when(restTemplate.getForObject(anyString(), eq(GeoCodingResponse[].class), anyString(), anyInt(), anyString()))
                .thenReturn(response);
        when(cityRepository.save(any(City.class))).thenAnswer(invocation -> invocation.getArgument(0));

        City result = geoCodingService.getCity(cityName, country);

        assertNotNull(result);
        assertEquals(cityName, result.getName());
        assertEquals(country, result.getCountry());
        assertEquals(lat, result.getCoordinates().latitude());
        assertEquals(lon, result.getCoordinates().longitude());
        
        verify(cityRepository).save(any(City.class));
    }

    @Test
    void getCity_whenCityNotFound_throwsException() {
        String cityName = "NonExistentCity";
        String country = "UK";
        when(cityRepository.findByNameAndCountry(cityName, country)).thenReturn(Optional.empty());
        when(cityRepository.findByName(cityName)).thenReturn(null);
        when(restTemplate.getForObject(anyString(), eq(GeoCodingResponse[].class), anyString(), anyInt(), anyString()))
                .thenReturn(new GeoCodingResponse[0]);

        assertThrows(CityNotFoundException.class, () -> geoCodingService.getCity(cityName, country));
    }

    @Test
    void getCity_whenApiReturnsError_throwsException() {
        String cityName = "ErrorCity";
        String country = "UK";
        when(cityRepository.findByNameAndCountry(cityName, country)).thenReturn(Optional.empty());
        when(cityRepository.findByName(cityName)).thenReturn(null);
        when(restTemplate.getForObject(anyString(), eq(GeoCodingResponse[].class), anyString(), anyInt(), anyString()))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        assertThrows(CityNotFoundException.class, () -> geoCodingService.getCity(cityName, country));
    }
}