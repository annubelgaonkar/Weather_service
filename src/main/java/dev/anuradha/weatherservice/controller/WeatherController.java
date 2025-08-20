package dev.anuradha.weatherservice.controller;

import dev.anuradha.weatherservice.dto.BaseResponseDTO;
import dev.anuradha.weatherservice.dto.WeatherRequestDTO;
import dev.anuradha.weatherservice.dto.WeatherResponseDTO;
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
    public ResponseEntity<BaseResponseDTO<WeatherResponseDTO>> getWeather(
            @RequestBody WeatherRequestDTO weatherRequest){

        WeatherResponseDTO weatherResponse = weatherService.getWeather(weatherRequest);

        BaseResponseDTO<WeatherResponseDTO> baseResponseDTO = new BaseResponseDTO<>(
                true,
                "Weather fetched successfully",
                weatherResponse
        );
        return ResponseEntity.ok(baseResponseDTO);
    }
}
