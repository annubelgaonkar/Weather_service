package dev.anuradha.weatherservice.dto;

import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeatherRequest {

    private String pincode;
    private LocalDate forDate;
}
