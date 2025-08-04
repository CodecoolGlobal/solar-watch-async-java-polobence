package com.codecool.solarwatch.model;

import java.time.LocalDate;

public record SunTimesResponse(String city,
                               LocalDate date,
                               SunTimes sunTimes,
                               String timezone) {
}
