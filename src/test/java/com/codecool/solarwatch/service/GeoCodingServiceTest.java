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
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GeoCodingServiceTest {

    @Mock
    private WebClient webClient;
    
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    
    @Mock
    private WebClient.ResponseSpec responseSpec;
    
    @Mock
    private CityRepository cityRepository;
    
    private GeoCodingService geoCodingService;

    @BeforeEach
    void setUp() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        geoCodingService = new GeoCodingService(webClient, cityRepository);
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
        verifyNoInteractions(webClient);
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
        when(responseSpec.bodyToMono(GeoCodingResponse[].class)).thenReturn(Mono.just(response));
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
        when(responseSpec.bodyToMono(GeoCodingResponse[].class)).thenReturn(Mono.just(new GeoCodingResponse[0]));

        assertThrows(CityNotFoundException.class, () -> geoCodingService.getCity(cityName, country));
    }

    @Test
    void getCity_whenApiReturnsError_throwsException() {
        String cityName = "ErrorCity";
        String country = "UK";
        when(cityRepository.findByNameAndCountry(cityName, country)).thenReturn(Optional.empty());
        when(cityRepository.findByName(cityName)).thenReturn(null);
        when(responseSpec.bodyToMono(GeoCodingResponse[].class))
                .thenReturn(Mono.error(new WebClientResponseException("Not Found", 404, "Not Found", null, null, null)));

        assertThrows(CityNotFoundException.class, () -> geoCodingService.getCity(cityName, country));
    }
}