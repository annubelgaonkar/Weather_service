package dev.anuradha.weatherservice.service;

import dev.anuradha.weatherservice.dto.OpenWeatherGeoResponse;
import dev.anuradha.weatherservice.dto.OpenWeatherMainResponse;
import dev.anuradha.weatherservice.dto.WeatherRequestDTO;
import dev.anuradha.weatherservice.dto.WeatherResponseDTO;
import dev.anuradha.weatherservice.entity.Pincode;
import dev.anuradha.weatherservice.entity.Weather;
import dev.anuradha.weatherservice.exception.WeatherServiceException;
import dev.anuradha.weatherservice.repository.PincodeRepository;
import dev.anuradha.weatherservice.repository.WeatherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

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
        Pincode pincode = pincodeRepository.findByPincode(request.getPincode())
                .orElseGet(() -> fetchAndSavePincode(request.getPincode()));

        // 2. Check if weather info exists for that date
        Optional<Weather> existing = weatherRepository
                .findByPincodeAndForDate(pincode, date);
        if (existing.isPresent()) {
            Weather w = existing.get();
            return WeatherResponseDTO.builder()
                    .pincode(request.getPincode())
                    .forDate(date.toString())
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

        try{
            var response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(geoUrl)
                            .queryParam("zip", pincode + ",IN")
                            .queryParam("appid", apiKey)
                            .build())
                    .retrieve()
                    .bodyToMono(OpenWeatherGeoResponse.class)
                    .block();

            if (response == null || response.getLat() == null || response.getLon() == null) {
                throw new WeatherServiceException(
                        "Pincode " + pincode + " is not supported by OpenWeather API.");
            }

            Pincode entity = Pincode.builder()
                    .pincode(pincode)
                    .latitude(response.getLat())
                    .longitude(response.getLon())
                    .build();

            return pincodeRepository.save(entity);

        }catch (WeatherServiceException ex){
            throw ex;
        }
        catch (Exception ex){
            throw new WeatherServiceException(
                    "Failed to fetch coordinates for pincode: " + pincode, ex
            );
        }
    }

    private WeatherResponseDTO fetchWeatherFromAPI(Pincode pincode, LocalDate date) {
        try {
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

            if (response == null || response.getMain() == null) {
                throw new WeatherServiceException(
                        "Weather data unavailable for pincode " + pincode.getPincode());
            }

            return new WeatherResponseDTO(
                    pincode.getPincode(),
                    date.toString(),
                    response.getMain().getTemp(),
                    response.getMain().getHumidity(),
                    response.getWeather().get(0).getDescription(),
                    null
            );

        } catch (Exception ex) {
            throw new WeatherServiceException(
                    "Failed to fetch weather data for pincode: " + pincode.getPincode(), ex);
        }
    }
}
