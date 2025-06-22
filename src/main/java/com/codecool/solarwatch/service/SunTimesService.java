package com.codecool.solarwatch.service;

import com.codecool.solarwatch.exception.CityNotFoundException;
import com.codecool.solarwatch.model.City;
import com.codecool.solarwatch.model.SunTimes;
import com.codecool.solarwatch.repository.SunTimesRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

@Service
public class SunTimesService {

    private final RestTemplate restTemplate;
    private final SunTimesRepository sunTimesRepository;
    private final CityService cityService;
    private static final String API_URL = "https://api.sunrise-sunset.org/json";

    public SunTimesService(RestTemplate restTemplate, 
                          SunTimesRepository sunTimesRepository,
                          CityService cityService) {
        this.restTemplate = restTemplate;
        this.sunTimesRepository = sunTimesRepository;
        this.cityService = cityService;
    }

    public SunTimes getSunTimes(String cityName, String country, LocalDate date) {
        // First try to find in database
        City city = cityService.getCity(cityName, country);
        Optional<SunTimes> existingSunTimes = sunTimesRepository.findByCityAndDate(city, date);
        if (existingSunTimes.isPresent()) {
            return existingSunTimes.get();
        }

        // If not found, fetch from API and save to database
        return fetchAndSaveSunTimes(city, date);
    }

    private SunTimes fetchAndSaveSunTimes(City city, LocalDate date) {
        String formattedDate = date.format(DateTimeFormatter.ISO_DATE);
        String url = String.format(
                "%s?lat=%s&lng=%s&date=%s&formatted=0",
                API_URL,
                city.getCoordinates().latitude(),
                city.getCoordinates().longitude(),
                formattedDate
        );

        Map response = restTemplate.getForObject(url, Map.class);

        if (response == null || !"OK".equals(response.get("status"))) {
            throw new RuntimeException("Failed to get sun times from API");
        }


        Map<String, String> results = (Map<String, String>) response.get("results");
        LocalTime sunrise = LocalTime.parse(results.get("sunrise"));
        LocalTime sunset = LocalTime.parse(results.get("sunset"));

        SunTimes sunTimes = new SunTimes(date, sunrise, sunset, city);
        return sunTimesRepository.save(sunTimes);
    }
}
