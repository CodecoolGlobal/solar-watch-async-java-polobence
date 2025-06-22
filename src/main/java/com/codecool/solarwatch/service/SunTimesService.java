package com.codecool.solarwatch.service;

import com.codecool.solarwatch.exception.CityNotFoundException;
import com.codecool.solarwatch.model.City;
import com.codecool.solarwatch.model.SunTimes;
import com.codecool.solarwatch.repository.SunTimesRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

@Service
public class SunTimesService {

    private final WebClient webClient;
    private final SunTimesRepository sunTimesRepository;
    private final CityService cityService;
    
    private static final DateTimeFormatter ISO_OFFSET_DATE_TIME = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    public SunTimesService(WebClient webClient, 
                          SunTimesRepository sunTimesRepository,
                          CityService cityService) {
        this.webClient = webClient;
        this.sunTimesRepository = sunTimesRepository;
        this.cityService = cityService;
    }

    public SunTimes getSunTimes(String cityName, String country, LocalDate date) {
        City city = cityService.getCity(cityName, country);
        Optional<SunTimes> existingSunTimes = sunTimesRepository.findByCityAndDate(city, date);
        if (existingSunTimes.isPresent()) {
            return existingSunTimes.get();
        }

        return fetchAndSaveSunTimes(city, date);
    }

    private SunTimes fetchAndSaveSunTimes(City city, LocalDate date) {
        String formattedDate = date.format(DateTimeFormatter.ISO_DATE);
        
        try {
            Map response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path("/json")
                    .queryParam("lat", city.getCoordinates().latitude())
                    .queryParam("lng", city.getCoordinates().longitude())
                    .queryParam("date", formattedDate)
                    .queryParam("formatted", 0)
                    .build())
                .retrieve()
                .bodyToMono(Map.class)
                .block();

            if (response == null || !"OK".equals(response.get("status"))) {
                throw new RuntimeException("Failed to get sun times from API");
            }

            @SuppressWarnings("unchecked")
            Map<String, String> results = (Map<String, String>) response.get("results");
            
            LocalTime sunrise = OffsetDateTime.parse(
                results.get("sunrise"), 
                ISO_OFFSET_DATE_TIME
            ).toLocalTime();
            
            LocalTime sunset = OffsetDateTime.parse(
                results.get("sunset"), 
                ISO_OFFSET_DATE_TIME
            ).toLocalTime();

            SunTimes sunTimes = new SunTimes(date, sunrise, sunset, city);
            return sunTimesRepository.save(sunTimes);
            
        } catch (WebClientResponseException e) {
            throw new RuntimeException("Error fetching sun times: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error processing sun times: " + e.getMessage(), e);
        }
    }
}
