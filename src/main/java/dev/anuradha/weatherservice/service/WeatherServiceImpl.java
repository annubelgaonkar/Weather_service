package dev.anuradha.weatherservice.service;

import dev.anuradha.weatherservice.dto.WeatherRequestDTO;
import dev.anuradha.weatherservice.dto.WeatherResponseDTO;
import dev.anuradha.weatherservice.entity.Pincode;
import dev.anuradha.weatherservice.entity.Weather;
import dev.anuradha.weatherservice.repository.PincodeRepository;
import dev.anuradha.weatherservice.repository.WeatherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WeatherServiceImpl implements WeatherService {

    private final PincodeRepository pincodeRepository;
    private final WeatherRepository weatherRepository;
    private final WebClient webClient; // injected from config

    @Value("${openweather.api.key}")
    private String apiKey;

    @Value("${openweather.api.url}")
    private String weatherUrl;

    @Value("${openweather.geo.url}")
    private String geoUrl;

    @Override
    public WeatherResponseDTO getWeather(WeatherRequestDTO request) {
        LocalDate date = LocalDate.parse(request.getForDate());

        // 1. Check if Pincode exists
        Pincode pincode = pincodeRepository.findByCode(request.getPincode())
                .orElseGet(() -> fetchAndSavePincode(request.getPincode()));

        // 2. Check if weather info exists for that date
        Optional<Weather> existing = weatherRepository.findByPincodeAndForDate(pincode, date);
        if (existing.isPresent()) {
            Weather w = existing.get();
            return WeatherResponseDTO.builder()
                    .pincode(request.getPincode())
                    .forDate(request.getForDate())
                    .temperature(w.getTemperature())
                    .humidity(w.getHumidity())
                    .description(w.getDescription())
                    .source("DB")
                    .build();
        }

        // 3. Fetch weather info from OpenWeather API
        WeatherResponseDTO apiResponse = fetchWeatherFromAPI(pincode, date);

        // 4. Save in DB
        Weather weather = Weather.builder()
                .pincode(pincode)
                .forDate(date)
                .temperature(apiResponse.getTemperature())
                .humidity(apiResponse.getHumidity())
                .description(apiResponse.getDescription())
                .source("API")
                .build();
        weatherRepository.save(weather);

        apiResponse.setSource("API");
        return apiResponse;
    }

    private Pincode fetchAndSavePincode(String pincode) {
        // Call OpenWeather Geo API for pincode → lat/long
        var response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(geoUrl)
                        .queryParam("zip", pincode + ",IN")
                        .queryParam("appid", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(OpenWeatherGeoResponse.class)
                .block();

        Pincode entity = Pincode.builder()
                .code(pincode)
                .latitude(response.getLat())
                .longitude(response.getLon())
                .build();
        return pincodeRepository.save(entity);
    }

    private WeatherResponseDTO fetchWeatherFromAPI(Pincode pincode, LocalDate date) {
        var response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(weatherUrl)
                        .queryParam("lat", pincode.getLatitude())
                        .queryParam("lon", pincode.getLongitude())
                        .queryParam("appid", apiKey)
                        .queryParam("units", "metric")
                        .build())
                .retrieve()
                .bodyToMono(OpenWeatherMainResponse.class)
                .block();

        return WeatherResponseDTO.builder()
                .pincode(pincode.getCode())
                .forDate(date.toString())
                .temperature(response.getMain().getTemp())
                .humidity(response.getMain().getHumidity())
                .description(response.getWeather().get(0).getDescription())
                .build();
    }
}
