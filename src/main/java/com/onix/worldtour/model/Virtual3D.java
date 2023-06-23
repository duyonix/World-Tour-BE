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
public class Virtual3D {
    @Column(name = "virtual_3d_x")
    private Double x;

    @Column(name = "virtual_3d_y")
    private Double y;

    @Column(name = "virtual_3d_z")
    private Double z;
}
