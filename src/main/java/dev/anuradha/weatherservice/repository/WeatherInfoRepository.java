package dev.anuradha.weatherservice.repository;

import dev.anuradha.weatherservice.entity.PincodeLocation;
import dev.anuradha.weatherservice.entity.WeatherInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface WeatherInfoRepository extends JpaRepository<WeatherInfo, Long> {

    Optional<WeatherInfo> findByPincodeLocationAndForDate(
            PincodeLocation location,
            LocalDate forDate);
}
