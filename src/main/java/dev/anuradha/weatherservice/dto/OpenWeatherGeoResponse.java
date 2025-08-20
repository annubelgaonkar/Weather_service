package dev.anuradha.weatherservice.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OpenWeatherGeoResponse {
    private String name;
    private Double lat;
    private Double lon;
    private String country;
}
