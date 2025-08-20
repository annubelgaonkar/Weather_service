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
public class Weather {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pincode_id", nullable = false)
    private Pincode pincodeLocation;

    @Column(name = "for_date", nullable = false)
    private LocalDate forDate;

    private Double temperature;
    private Integer humidity;
    private String description;

    @Column(nullable = false)
    private String source;
}
