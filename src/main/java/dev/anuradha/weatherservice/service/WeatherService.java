package dev.anuradha.weatherservice.service;

import dev.anuradha.weatherservice.dto.WeatherRequestDTO;
import dev.anuradha.weatherservice.dto.WeatherResponseDTO;

public interface WeatherService {
    WeatherResponseDTO getWeather(WeatherRequestDTO weatherRequest);
}
