package dev.anuradha.weatherservice.dto;

import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WeatherResponseDTO {

    private String pincode;
    private String forDate;
    private Double temperature;
    private Integer humidity;
    private String description;
    private String source;
}
