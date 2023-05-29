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

    @Column(name = "code", nullable = false, columnDefinition = "TEXT")
    private String code;

    @Column(name = "tld", columnDefinition = "TEXT")
    private String tld;

    @Column(name = "capital", columnDefinition = "TEXT")
    private String capital;

    @Column(name = "language", columnDefinition = "TEXT")
    private String language;

    @Column(name = "currency", columnDefinition = "TEXT")
    private String currency;

    @Column(name = "timezone", columnDefinition = "TEXT")
    private String timezone;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "country", "category", "children", "parent", "costumes", "sceneSpots"})
    private Region region;
}
