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
import java.util.List;
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

    public SunTimes createSunTimes(SunTimes sunTimes) {
        Optional<SunTimes> existingSunTimes = sunTimesRepository.findByCityAndDate(
            sunTimes.getCity(), 
            sunTimes.getDate()
        );
        
        if (existingSunTimes.isPresent()) {
            throw new RuntimeException("Sun times already exist for this city and date");
        }
        
        return sunTimesRepository.save(sunTimes);
    }
    
    public SunTimes updateSunTimes(Long id, SunTimes sunTimesDetails) {
        SunTimes sunTimes = sunTimesRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Sun times not found with id: " + id));
            
        sunTimes.setDate(sunTimesDetails.getDate());
        sunTimes.setSunrise(sunTimesDetails.getSunrise());
        sunTimes.setSunset(sunTimesDetails.getSunset());
        
        if (sunTimesDetails.getCity() != null &&
            (sunTimes.getCity() == null || 
             !sunTimes.getCity().getId().equals(sunTimesDetails.getCity().getId()))) {
            City city = cityService.getCityById(sunTimesDetails.getCity().getId());
            sunTimes.setCity(city);
        }
        
        return sunTimesRepository.save(sunTimes);
    }
    
    public void deleteSunTimes(Long id) {
        SunTimes sunTimes = sunTimesRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Sun times not found with id: " + id));
            
        sunTimesRepository.delete(sunTimes);
    }
    
    public SunTimes getSunTimesById(Long id) {
        return sunTimesRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Sun times not found with id: " + id));
    }
    
    public List<SunTimes> getAllSunTimes() {
        return sunTimesRepository.findAll();
    }
}
