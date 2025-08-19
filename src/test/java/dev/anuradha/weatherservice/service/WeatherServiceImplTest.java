package dev.anuradha.weatherservice.service;

import dev.anuradha.weatherservice.dto.WeatherRequest;
import dev.anuradha.weatherservice.dto.WeatherResponse;
import dev.anuradha.weatherservice.entity.PincodeLocation;
import dev.anuradha.weatherservice.entity.WeatherInfo;
import dev.anuradha.weatherservice.repository.PincodeLocationRepository;
import dev.anuradha.weatherservice.repository.WeatherInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

class WeatherServiceImplTest {

    @Mock
    private PincodeLocationRepository pincodeLocationRepository;

    @Mock
    private WeatherInfoRepository weatherInfoRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private WeatherServiceImpl weatherService;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testgetWeather_FromCache(){
        PincodeLocation location =
                new PincodeLocation(1L, "560017",
                        18.24, 74.56);

        WeatherInfo weatherInfo = WeatherInfo.builder()
                .pincodeLocation(location)
                .forDate(LocalDate.of(2025,8,19))
                .temperature(23.0)
                .humidity(60)
                .weatherDescription("overcast clouds")
                .fetchedAt(LocalDateTime.now())
                .build();

        when(pincodeLocationRepository.findByPincode("590010"))
                .thenReturn(Optional.of(location));
        when(weatherInfoRepository.findByPincodeLocationAndForDate(location,
                LocalDate.of(2025,
                        8,
                        19)))
                .thenReturn(Optional.of(weatherInfo));

        //when
        WeatherResponse response = weatherService.getWeather(
                new WeatherRequest("590010",
                        LocalDate.of(2025,
                                8,
                                19)));

        //then
        assertNotNull(response);
        assertEquals("590010", response.getPincode());
        assertEquals(23, response.getTemperature());
        assertEquals("overcast clouds", response.getDescription());
        assertEquals(weatherInfoRepository,never()).save(any());

    }

}