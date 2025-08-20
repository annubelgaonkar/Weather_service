package dev.anuradha.weatherservice.exception;
import dev.anuradha.weatherservice.dto.BaseResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Handle custom WeatherServiceException
    @ExceptionHandler(WeatherServiceException.class)
    public ResponseEntity<BaseResponseDTO<Object>> handleWeatherServiceException(
            WeatherServiceException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new BaseResponseDTO<>(
                false,
                ex.getMessage(),
                null));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<BaseResponseDTO<Object>> handleInvalidJson(
            HttpMessageNotReadableException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new BaseResponseDTO<>(
                        false,
                        "Invalid input: " + ex.getMostSpecificCause().getMessage(),
                        null
                ));
    }

    // Handle all other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponseDTO<Object>> handleGeneralException(
            Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new BaseResponseDTO<>(
                        false,
                        "An unexpected error occurred: " + ex.getMessage(), null
                ));
    }
}
