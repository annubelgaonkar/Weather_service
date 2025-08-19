package dev.anuradha.weatherservice.dto;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BaseResponseDTO<T> {

    private boolean success;
    private String message;
    private T data;
}
