package dev.anuradha.weatherservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class OpenWeatherMainResponse {
    private Main main;
    private List<WeatherDesc> weather;

    @Getter
    @Setter
    public static class Main {
        private Double temp;
        private Integer humidity;
    }

    @Getter
    @Setter
    public static class WeatherDesc {
        private String main;
        private String description;
    }
}
