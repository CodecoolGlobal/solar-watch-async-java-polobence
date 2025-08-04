package com.codecool.solarwatch.service;

import com.codecool.solarwatch.model.City;
import com.codecool.solarwatch.model.Coordinates;
import com.codecool.solarwatch.repository.CityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GeoCodingServiceTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private CityRepository cityRepository;

    private GeoCodingService geoCodingService;

    @BeforeEach
    void setUp() {
        when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);

        geoCodingService = new GeoCodingService(webClientBuilder, cityRepository);
        ReflectionTestUtils.setField(geoCodingService, "apiKey", "test-api-key");
    }

    @Test
    void getCity_whenCityInDatabase_returnsCity() {
        String cityName = "London";
        String country = "UK";
        City expectedCity = new City(cityName, "", country, new Coordinates(51.5074, -0.1278));
        when(cityRepository.findByNameAndCountry(cityName, country))
                .thenReturn(Optional.of(expectedCity));

        City result = geoCodingService.getCity(cityName, country);

        assertNotNull(result);
        assertEquals(expectedCity, result);
        verify(cityRepository).findByNameAndCountry(cityName, country);
        verifyNoInteractions(webClient);
    }
}