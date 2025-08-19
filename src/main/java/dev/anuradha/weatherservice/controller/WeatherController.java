package dev.anuradha.weatherservice.controller;

import dev.anuradha.weatherservice.dto.WeatherRequest;
import dev.anuradha.weatherservice.dto.WeatherResponse;
import dev.anuradha.weatherservice.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    @PostMapping
    public ResponseEntity<WeatherResponse> getWeather(@RequestBody WeatherRequest weatherRequest){
        WeatherResponse weatherResponse = weatherService.getWeather(weatherRequest);
        return ResponseEntity.ok(weatherResponse);
    }
}
