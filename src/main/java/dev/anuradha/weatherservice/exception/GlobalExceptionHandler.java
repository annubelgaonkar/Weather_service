package dev.anuradha.weatherservice.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import dev.anuradha.weatherservice.dto.BaseResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

@ControllerAdvice
public class GlobalExceptionHandler {


    // Handle custom WeatherServiceException
    @ExceptionHandler(WeatherServiceException.class)
    public ResponseEntity<BaseResponseDTO<Object>> handleWeatherServiceException(WeatherServiceException ex) {
        BaseResponseDTO<Object> response = new BaseResponseDTO<>(
                false,
                ex.getMessage(),
                null
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<BaseResponseDTO<Object>> handleInvalidJson(HttpMessageNotReadableException ex) {
        return ResponseEntity.ok(new BaseResponseDTO<>(false,
                "Invalid input: " + ex.getMostSpecificCause().getMessage(), null));
    }

    // Handle all other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponseDTO<Object>> handleGeneralException(Exception ex) {
        BaseResponseDTO<Object> response = new BaseResponseDTO<>(
                false,
                "An unexpected error occurred: " + ex.getMessage(),
                null
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
