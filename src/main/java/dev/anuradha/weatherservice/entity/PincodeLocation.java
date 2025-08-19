package dev.anuradha.weatherservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pincode_location")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PincodeLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String pincode;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;
}
