package dev.anuradha.weatherservice.dto;

import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeatherRequestDTO {

    @NotBlank(message = "Pincode is required")
    private String pincode;

    @NotBlank(message = "Date is required")
    @Pattern(
            regexp = "\\d{4}-\\d{2}-\\d{2}",
            message = "Date must be in format yyyy-MM-dd"
    )
    private String forDate;
}
