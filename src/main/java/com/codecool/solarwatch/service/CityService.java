package com.codecool.solarwatch.service;

import com.codecool.solarwatch.exception.CityNotFoundException;
import com.codecool.solarwatch.model.City;
import com.codecool.solarwatch.model.Coordinates;
import com.codecool.solarwatch.repository.CityRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CityService {

    private final CityRepository cityRepository;
    private final GeoCodingService geoCodingService;

    public CityService(CityRepository cityRepository, GeoCodingService geoCodingService) {
        this.cityRepository = cityRepository;
        this.geoCodingService = geoCodingService;
    }

    public City getCity(String cityName, String country) {
        // First try to find by both name and country if country is provided
        if (country != null && !country.isBlank()) {
            Optional<City> existingCity = cityRepository.findByNameAndCountry(cityName, country);
            if (existingCity.isPresent()) {
                return existingCity.get();
            }
        }

        // If not found, try to find just by name
        Optional<City> cityOptional = Optional.ofNullable(cityRepository.findByName(cityName));
        if (cityOptional.isPresent()) {
            City city = cityOptional.get();
            // If country was provided but doesn't match, throw exception
            if (country != null && !country.isBlank() && !city.getCountry().equalsIgnoreCase(country)) {
                throw new CityNotFoundException("City " + cityName + " not found in country: " + country);
            }
            return city;
        }

        // If still not found, fetch from API
        City city = geoCodingService.getCity(cityName, country);
        
        // Save the city to the database
        return cityRepository.save(city);
    }

    public City getCityById(Long id) {
        return cityRepository.findById(id)
                .orElseThrow(() -> new CityNotFoundException("City not found with id: " + id));
    }
}
