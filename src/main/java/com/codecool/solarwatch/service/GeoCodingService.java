package com.codecool.solarwatch.service;

import com.codecool.solarwatch.exception.CityNotFoundException;
import com.codecool.solarwatch.model.City;
import com.codecool.solarwatch.model.Coordinates;
import com.codecool.solarwatch.repository.CityRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class GeoCodingService {

    private final RestTemplate restTemplate;
    private final CityRepository cityRepository;
    
    @Value("${geo.api.key:${GEO_API_KEY:}}")
    private String apiKey;
    
    private static final String GEOCODING_API_URL = "http://api.openweathermap.org/geo/1.0/direct";

    public GeoCodingService(RestTemplate restTemplate, CityRepository cityRepository) {
        this.restTemplate = restTemplate;
        this.cityRepository = cityRepository;
    }

    public City getCity(String cityName, String country) {
        // First try to find by both name and country if country is provided
        if (country != null && !country.isBlank()) {
            Optional<City> cityOptional = cityRepository.findByNameAndCountry(cityName, country);
            if (cityOptional.isPresent()) {
                return cityOptional.get();
            }
        }

        // If not found, try to find just by name
        Optional<City> cityOptional = Optional.ofNullable(cityRepository.findByName(cityName));
        if (cityOptional.isPresent()) {
            return cityOptional.get();
        }

        // If still not found, fetch from API
        try {
            String query = country != null && !country.isBlank() 
                ? String.format("%s,%s", cityName, country) 
                : cityName;
                
            GeoCodingResponse[] response = restTemplate.getForObject(
                    GEOCODING_API_URL + "?q={query}&limit={limit}&appid={apiKey}",
                    GeoCodingResponse[].class,
                    query, 1, apiKey
            );

            if (response == null || response.length == 0) {
                throw new CityNotFoundException("City not found: " + cityName + (country != null ? ", " + country : ""));
            }

            GeoCodingResponse geoData = response[0];
            Coordinates coordinates = new Coordinates(geoData.getLat(), geoData.getLon());
            City city = new City(geoData.getName(), 
                              geoData.getState() != null ? geoData.getState() : "", 
                              geoData.getCountry(), 
                              coordinates);
            return cityRepository.save(city);
        } catch (HttpClientErrorException e) {
            throw new CityNotFoundException("Error fetching city data: " + e.getMessage());
        } catch (Exception e) {
            throw new CityNotFoundException("Error processing city data: " + e.getMessage());
        }
    }

    private static class GeoCodingResponse {
        private String name;
        private double lat;
        private double lon;
        private String country;
        private String state;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public double getLat() { return lat; }
        public void setLat(double lat) { this.lat = lat; }
        public double getLon() { return lon; }
        public void setLon(double lon) { this.lon = lon; }
        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
    }
}
