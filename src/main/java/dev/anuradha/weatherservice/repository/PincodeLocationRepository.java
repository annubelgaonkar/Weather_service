package dev.anuradha.weatherservice.repository;

import dev.anuradha.weatherservice.entity.PincodeLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PincodeLocationRepository extends JpaRepository<PincodeLocation, Long> {
    Optional<PincodeLocation> findByPincode(String pincode);
}
