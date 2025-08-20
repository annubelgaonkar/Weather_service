package dev.anuradha.weatherservice.service;

import dev.anuradha.weatherservice.dto.WeatherRequestDTO;
import dev.anuradha.weatherservice.dto.WeatherResponseDTO;
import dev.anuradha.weatherservice.entity.Pincode;
import dev.anuradha.weatherservice.entity.Weather;
import dev.anuradha.weatherservice.repository.PincodeRepository;
import dev.anuradha.weatherservice.repository.WeatherRepository;
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
import static org.mockito.Mockito.*;

class WeatherServiceImplTest {

    @Mock
    private PincodeRepository pincodeLocationRepository;

    @Mock
    private WeatherRepository weatherInfoRepository;

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
        Pincode location =
                new Pincode(1L, "590010",
                        18.24, 74.56);

        Weather weatherInfo = Weather.builder()
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
        WeatherResponseDTO response = weatherService.getWeather(
                new WeatherRequestDTO("590010",
                        LocalDate.of(2025,
                                8,
                                19)));

        //then
        assertNotNull(response);
        assertEquals("590010", response.getPincode());
        assertEquals(23, response.getTemperature());
        assertEquals("overcast clouds", response.getDescription());
        verify(weatherInfoRepository,never()).save(any());

    }

}