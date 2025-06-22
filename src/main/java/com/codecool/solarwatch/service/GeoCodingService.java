package com.codecool.solarwatch.service;

import com.codecool.solarwatch.exception.CityNotFoundException;
import com.codecool.solarwatch.model.City;
import com.codecool.solarwatch.model.Coordinates;
import com.codecool.solarwatch.model.GeoCodingResponse;
import com.codecool.solarwatch.repository.CityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Optional;

@Service
public class GeoCodingService {
    private static final Logger logger = LoggerFactory.getLogger(GeoCodingService.class);
    
    private final WebClient webClient;
    private final CityRepository cityRepository;
    
    @Value("${geo.api.key:${GEO_API_KEY:}}")
    private String apiKey;
    
    private static final String GEOCODING_API_URL = "https://api.openweathermap.org/geo/1.0/direct";

    public GeoCodingService(WebClient.Builder webClientBuilder, CityRepository cityRepository) {
        this.webClient = webClientBuilder.baseUrl(GEOCODING_API_URL).build();
        this.cityRepository = cityRepository;
    }

    public City getCity(String cityName, String country) {
        try {
            // First try to find in database
            if (country != null && !country.isBlank()) {
                Optional<City> cityOptional = cityRepository.findByNameAndCountry(cityName, country);
                if (cityOptional.isPresent()) {
                    return cityOptional.get();
                }
            }

            Optional<City> cityOptional = Optional.ofNullable(cityRepository.findByName(cityName));
            if (cityOptional.isPresent()) {
                return cityOptional.get();
            }

            String query = country != null && !country.isBlank()
                ? String.format("%s,%s", cityName, country) 
                : cityName;
                
            logger.info("Fetching coordinates for: {}", query);
                
            GeoCodingResponse[] response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                    .queryParam("q", query)
                    .queryParam("limit", 1)
                    .queryParam("appid", apiKey)
                    .build())
                .retrieve()
                .bodyToMono(GeoCodingResponse[].class)
                .block();

            if (response == null || response.length == 0) {
                throw new CityNotFoundException("City not found: " + cityName + (country != null ? ", " + country : ""));
            }

            GeoCodingResponse geoData = response[0];
            Coordinates coordinates = new Coordinates(geoData.getLat(), geoData.getLon());
            City city = new City(
                geoData.getName(), 
                geoData.getState() != null ? geoData.getState() : "", 
                geoData.getCountry(), 
                coordinates
            );
            
            logger.info("Fetched city data: {}", city);
            return cityRepository.save(city);
            
        } catch (WebClientResponseException e) {
            logger.error("Error fetching city data: {}", e.getMessage());
            throw new CityNotFoundException("Error fetching city data: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error processing city data: {}", e.getMessage(), e);
            throw new CityNotFoundException("Error processing city data: " + e.getMessage());
        }
    }
}
