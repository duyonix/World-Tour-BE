package com.onix.worldtour.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity(name = "Weather")
@Table(name = "weather")
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class Weather {
    @Id
    @SequenceGenerator(name = "weather_sequence", sequenceName = "weather_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "weather_sequence")
    @Column(name = "id", updatable = false)
    private Integer id;

    @Column(name = "name", nullable = false, columnDefinition = "TEXT")
    private String name;

    @Column(name = "vietnamese_name", nullable = false, columnDefinition = "TEXT")
    private String vietnameseName;

    @Column(name = "background", nullable = false, columnDefinition = "TEXT")
    private String background;
}
