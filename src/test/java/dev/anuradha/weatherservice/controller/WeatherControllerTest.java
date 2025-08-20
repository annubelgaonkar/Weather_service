package dev.anuradha.weatherservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.anuradha.weatherservice.dto.WeatherRequestDTO;
import dev.anuradha.weatherservice.dto.WeatherResponseDTO;
import dev.anuradha.weatherservice.service.WeatherService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDate;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class WeatherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private WeatherService weatherService;


    @Test
    void testWeatherEndpoint_Success() throws Exception{

        WeatherRequestDTO request = new WeatherRequestDTO(
                "590010",
                "2025-08-19"
        );

        WeatherResponseDTO response = WeatherResponseDTO.builder()
                .pincode("590010")
                .forDate("2025-08-19")
                .temperature(30.0)
                .humidity(80)
                .description("overcast clouds")
                .build();

        //when(weatherService.getWeather(request)).thenReturn(response);

        when(weatherService.getWeather(any(WeatherRequestDTO.class)))
                .thenReturn(response);
        mockMvc.perform(post("/api/weather")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Weather fetched successfully"))
                .andExpect(jsonPath("$.data.pincode").value("590010"))
                .andExpect(jsonPath("$.data.forDate").value("2025-08-19"))
                .andExpect(jsonPath("$.data.temperature").value(30.0))
                .andExpect(jsonPath("$.data.humidity").value(80))
                .andExpect(jsonPath("$.data.description").value("overcast clouds"));
    }

    @Test
    void testWeatherEndpoint_Failure() throws Exception {
        // Given: service throws exception
        Mockito.when(weatherService.getWeather(any(WeatherRequestDTO.class)))
                .thenThrow(new RuntimeException("Something went wrong"));

        WeatherRequestDTO request = new WeatherRequestDTO("560099", LocalDate.now().toString());

        // When + Then
        mockMvc.perform(post("/api/weather")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message")
                        .value("An unexpected error occurred: Something went wrong"));

    }
}