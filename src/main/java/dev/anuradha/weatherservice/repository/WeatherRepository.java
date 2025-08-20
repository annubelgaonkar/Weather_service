package dev.anuradha.weatherservice.repository;

import dev.anuradha.weatherservice.entity.Pincode;
import dev.anuradha.weatherservice.entity.Weather;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface WeatherRepository extends JpaRepository<Weather, Long> {

    Optional<Weather> findByPincodeAndForDate(
            Pincode location,
            LocalDate forDate);
}
