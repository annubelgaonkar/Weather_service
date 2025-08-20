package dev.anuradha.weatherservice.dto;

import lombok.*;

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
