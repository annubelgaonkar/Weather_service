package dev.anuradha.weatherservice.service;

import dev.anuradha.weatherservice.dto.WeatherRequest;
import dev.anuradha.weatherservice.dto.WeatherResponse;

public interface WeatherService {
    WeatherResponse getWeather(WeatherRequest weatherRequest);
}
