package dev.anuradha.weatherservice.service;

import dev.anuradha.weatherservice.dto.WeatherRequest;
import dev.anuradha.weatherservice.dto.WeatherResponse;
import dev.anuradha.weatherservice.entity.PincodeLocation;
import dev.anuradha.weatherservice.entity.WeatherInfo;
import dev.anuradha.weatherservice.repository.PincodeLocationRepository;
import dev.anuradha.weatherservice.repository.WeatherInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WeatherServiceImpl implements WeatherService {

    private final PincodeLocationRepository pincodeLocationRepository;
    private final WeatherInfoRepository weatherInfoRepository;
    private final RestTemplate restTemplate;

    @Value("${openweather.api.key}")
    private String apiKey;

    @Override
    public WeatherResponse getWeather(WeatherRequest request) {
        // Step 1: Find location by pincode
        PincodeLocation location = pincodeLocationRepository.findByPincode(request.getPincode())
                .orElseGet(() -> fetchAndSaveLocation(request.getPincode()));

        // Step 2: Check DB for cached weather info
        Optional<WeatherInfo> cachedWeather = weatherInfoRepository.findByPincodeLocationAndForDate(location, request.getForDate());
        if (cachedWeather.isPresent()) {
            return mapToResponse(request.getPincode(), cachedWeather.get());
        }

        // Step 3: Call OpenWeather API
        String url = String.format(
                "https://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&appid=%s&units=metric",
                location.getLatitude(), location.getLongitude(), apiKey);

        var response = restTemplate.getForObject(url, Map.class);

        Double temp = Double.valueOf(((Map<String, Object>) response.get("main")).get("temp").toString());
        Integer humidity = (Integer) ((Map<String, Object>) response.get("main")).get("humidity");
        String description = (String) ((Map<String, Object>) ((java.util.List) response.get("weather")).get(0)).get("description");

        // Step 4: Save to DB
        WeatherInfo weatherInfo = WeatherInfo.builder()
                .pincodeLocation(location)
                .forDate(request.getForDate())
                .temperature(temp)
                .humidity(humidity)
                .weatherDescription(description)
                .fetchedAt(LocalDateTime.now())
                .build();

        weatherInfoRepository.save(weatherInfo);

        return mapToResponse(request.getPincode(), weatherInfo);
    }

    private PincodeLocation fetchAndSaveLocation(String pincode) {
        // Call OpenWeather Geocoding API
        String url = String.format("http://api.openweathermap.org/geo/1.0/zip?zip=%s,IN&appid=%s", pincode, apiKey);

        var response = restTemplate.getForObject(url, Map.class);

        Double lat = Double.valueOf(response.get("lat").toString());
        Double lon = Double.valueOf(response.get("lon").toString());

        PincodeLocation location = PincodeLocation.builder()
                .pincode(pincode)
                .latitude(lat)
                .longitude(lon)
                .build();

        return pincodeLocationRepository.save(location);
    }

    private WeatherResponse mapToResponse(String pincode, WeatherInfo weatherInfo) {
        return WeatherResponse.builder()
                .pincode(pincode)
                .forDate(weatherInfo.getForDate().toString())
                .temperature(weatherInfo.getTemperature())
                .humidity(weatherInfo.getHumidity())
                .description(weatherInfo.getWeatherDescription())
                .build();
    }
}
