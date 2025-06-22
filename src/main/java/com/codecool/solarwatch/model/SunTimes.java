package com.codecool.solarwatch.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "sun_times")
public class SunTimes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private LocalDate date;
    
    @Column(nullable = false)
    private LocalTime sunrise;
    
    @Column(nullable = false)
    private LocalTime sunset;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;
    
    public SunTimes() {
    }
    
    public SunTimes(LocalDate date, LocalTime sunrise, LocalTime sunset, City city) {
        this.date = date;
        this.sunrise = sunrise;
        this.sunset = sunset;
        this.city = city;
    }
    
    // Getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public LocalTime getSunrise() {
        return sunrise;
    }
    
    public void setSunrise(LocalTime sunrise) {
        this.sunrise = sunrise;
    }
    
    public LocalTime getSunset() {
        return sunset;
    }
    
    public void setSunset(LocalTime sunset) {
        this.sunset = sunset;
    }
    
    public City getCity() {
        return city;
    }
    
    public void setCity(City city) {
        this.city = city;
    }
    
    @Override
    public String toString() {
        return "SunTimes{" +
                "id=" + id +
                ", date=" + date +
                ", sunrise=" + sunrise +
                ", sunset=" + sunset +
                ", cityId=" + (city != null ? city.getId() : null) +
                '}';
    }
}
