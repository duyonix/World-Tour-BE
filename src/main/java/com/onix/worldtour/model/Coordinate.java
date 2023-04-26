package com.onix.worldtour.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Coordinate {
    @Column(name = "lattitude")
    private Double lattitude;

    @Column(name = "longitude")
    private Double longitude;
}
