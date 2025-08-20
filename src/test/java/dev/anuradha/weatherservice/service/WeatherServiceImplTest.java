package dev.anuradha.weatherservice.service;

import dev.anuradha.weatherservice.dto.OpenWeatherGeoResponse;
import dev.anuradha.weatherservice.dto.WeatherRequestDTO;
import dev.anuradha.weatherservice.dto.WeatherResponseDTO;
import dev.anuradha.weatherservice.entity.Pincode;
import dev.anuradha.weatherservice.entity.Weather;
import dev.anuradha.weatherservice.repository.PincodeRepository;
import dev.anuradha.weatherservice.repository.WeatherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class WeatherServiceImplTest {

    @Mock
    private PincodeRepository pincodeRepository;

    @Mock
    private WeatherRepository weatherRepository;

    @Mock
    private WebClient webClient;

    @InjectMocks
    private WeatherServiceImpl weatherService;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testgetWeather_FromCache(){

        String pincode = "560017";
        LocalDate date = LocalDate.of(2025, 8,19);

        Pincode pincodeEntity = Pincode.builder()
                .id(1L)
                .pincode(pincode)
                .latitude(12.97)
                .longitude(77.59)
                .build();

        Weather weather = Weather.builder()
                .pincode(pincodeEntity)
                .forDate(date)
                .temperature(21.4)
                .humidity(50)
                .description("Overcast clouds")
                .source("DB")
                .build();

        when(pincodeRepository.findByPincode(pincode))
                .thenReturn(Optional.of(pincodeEntity));
        when(weatherRepository.findByPincodeAndForDate(pincodeEntity, date))
                .thenReturn(Optional.of(weather));

        WeatherRequestDTO request = new WeatherRequestDTO(pincode, date.toString());

        //when
        WeatherResponseDTO response = weatherService.getWeather(request);

        //then
        assertNotNull(response);
        assertEquals(pincode, response.getPincode());
        assertEquals("Overcast clouds", response.getDescription());
        assertEquals("DB", response.getSource());

        verify(weatherRepository, never()).save(any());

    }

    @Test
    void testGetWeather_FromApi_WhenCacheMiss(){

        String pincode = "560018";
        LocalDate date = LocalDate.of(2025, 8, 20);

        Pincode pincodeEntity = Pincode.builder()
                .id(2L)
                .pincode(pincode)
                .latitude(12.91)
                .longitude(77.64)
                .build();

        when(pincodeRepository.findByPincode(pincode))
                .thenReturn(Optional.of(pincodeEntity));

        when(weatherRepository.findByPincodeAndForDate(
                pincodeEntity, date)).thenReturn(Optional.empty());

        // Mock WebClient response
        var mockRequestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        var mockRequestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        var mockResponseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(mockRequestHeadersUriSpec);
        when(mockRequestHeadersUriSpec.uri(ArgumentMatchers.<java.util.function.Function>any()))
                .thenReturn(mockRequestHeadersSpec);
        when(mockRequestHeadersSpec.retrieve()).thenReturn(mockResponseSpec);

        dev.anuradha.weatherservice.dto.OpenWeatherMainResponse fakeApiResponse =
                new dev.anuradha.weatherservice.dto.OpenWeatherMainResponse();
        dev.anuradha.weatherservice.dto.OpenWeatherMainResponse.Main main =
                new dev.anuradha.weatherservice.dto.OpenWeatherMainResponse.Main();
        main.setTemp(30.0);
        main.setHumidity(65);
        fakeApiResponse.setMain(main);

        dev.anuradha.weatherservice.dto.OpenWeatherMainResponse.WeatherDesc desc =
                new dev.anuradha.weatherservice.dto.OpenWeatherMainResponse.WeatherDesc();
        desc.setDescription("scattered clouds");
        fakeApiResponse.setWeather(java.util.List.of(desc));

        when(mockResponseSpec.bodyToMono(dev.anuradha.weatherservice
                .dto.OpenWeatherMainResponse.class))
                .thenReturn(reactor.core.publisher.Mono.just(fakeApiResponse));

        WeatherRequestDTO request = new WeatherRequestDTO(pincode, date.toString());

        // When
        WeatherResponseDTO response = weatherService.getWeather(request);

        // Then
        assertNotNull(response);
        assertEquals(pincode, response.getPincode());
        assertEquals(30.0, response.getTemperature());
        assertEquals(65, response.getHumidity());
        assertEquals("scattered clouds", response.getDescription());
        assertEquals("API", response.getSource());

        // Verify DB save happened
        verify(weatherRepository, times(1)).save(any(Weather.class));
    }

    //negative tests
    @Test
    void testGetWeather_InvalidPincode_ShouldThrowException() {
        // Given
        String invalidPincode = "999999";
        LocalDate date = LocalDate.of(2025, 8, 21);

        when(pincodeRepository.findByPincode(invalidPincode)).thenReturn(Optional.empty());

        // Mock WebClient returning empty (simulating invalid pincode)
        var mockRequestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        var mockRequestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        var mockResponseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(mockRequestHeadersUriSpec);
        when(mockRequestHeadersUriSpec.uri(ArgumentMatchers.<java.util.function.Function>any()))
                .thenReturn(mockRequestHeadersSpec);
        when(mockRequestHeadersSpec.retrieve()).thenReturn(mockResponseSpec);
        when(mockResponseSpec.bodyToMono(OpenWeatherGeoResponse.class))
                .thenReturn(reactor.core.publisher.Mono.empty());

        WeatherRequestDTO request = new WeatherRequestDTO(invalidPincode, date.toString());

        // Expect WeatherServiceException
        assertThrows(dev.anuradha.weatherservice.exception.WeatherServiceException.class,
                () -> weatherService.getWeather(request));
    }

    @Test
    void testGetWeather_ApiFailure_ShouldThrowException() {
        // Given
        String pincode = "560020";
        LocalDate date = LocalDate.of(2025, 8, 22);

        Pincode pincodeEntity = Pincode.builder()
                .id(3L)
                .pincode(pincode)
                .latitude(12.9)
                .longitude(77.6)
                .build();

        when(pincodeRepository.findByPincode(pincode))
                .thenReturn(Optional.of(pincodeEntity));
        when(weatherRepository.findByPincodeAndForDate(pincodeEntity, date))
                .thenReturn(Optional.empty());

        // Mock WebClient throwing exception
        var mockRequestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        var mockRequestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        when(webClient.get()).thenReturn(mockRequestHeadersUriSpec);
        when(mockRequestHeadersUriSpec.uri(ArgumentMatchers.<java.util.function.Function>any()))
                .thenReturn(mockRequestHeadersSpec);
        when(mockRequestHeadersSpec.retrieve()).thenThrow(new RuntimeException("API down"));

        WeatherRequestDTO request = new WeatherRequestDTO(pincode, date.toString());

        // Expect
        assertThrows(dev.anuradha.weatherservice.exception.WeatherServiceException.class,
                () -> weatherService.getWeather(request));
    }

}