package com.onix.worldtour.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity(name = "Country")
@Table(name = "country")
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class Country {
    @Id
    @SequenceGenerator(name = "country_sequence", sequenceName = "country_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "country_sequence")
    @Column(name = "id", updatable = false)
    private Integer id;

    @Column(name = "code", nullable = false, unique = true, columnDefinition = "TEXT")
    private String code;

    @Column(name = "capital", nullable = false, columnDefinition = "TEXT")
    private String capital;

    @Column(name = "language", nullable = false, columnDefinition = "TEXT")
    private String language;

    @Column(name = "currency", nullable = false, columnDefinition = "TEXT")
    private String currency;

    @Column(name = "timezone", nullable = false, columnDefinition = "TEXT")
    private String timezone;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", nullable = false)
    @JsonIgnoreProperties("country")
    private Region region;
}