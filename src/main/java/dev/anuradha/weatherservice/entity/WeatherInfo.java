package dev.anuradha.weatherservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeatherInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pincode_id", nullable = false)
    private PincodeLocation pincodeLocation;

    @Column(name = "for_date", nullable = false)
    private LocalDate forDate;

    @Column(nullable = false)
    private Double temperature;

    @Column(nullable = false)
    private Integer humidity;

    @Column(nullable = false)
    private String weatherDescription;

    @Column(name = "fetched_at", nullable = false)
    private LocalDateTime fetchedAt;
}
