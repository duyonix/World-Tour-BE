package com.onix.worldtour.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity(name = "SceneSpot")
@Table(name = "scene_spot")
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class SceneSpot {
    @Id
    @SequenceGenerator(name = "scene_spot_sequence", sequenceName = "scene_spot_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "scene_spot_sequence")
    @Column(name = "id", updatable = false)
    private Integer id;

    @Column(name = "name", nullable = false, columnDefinition = "TEXT")
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "picture", nullable = false, columnDefinition = "TEXT")
    private String picture;

    @Column(name = "panorama", columnDefinition = "TEXT")
    private String panorama;

    @Column(name = "panorama_type")
    @Enumerated(EnumType.STRING)
    private PanoramaType panoramaType;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "x", column = @Column(name = "virtual_3d_x")),
            @AttributeOverride(name = "y", column = @Column(name = "virtual_3d_y")),
            @AttributeOverride(name = "z", column = @Column(name = "virtual_3d_z"))
    })
    private Virtual3D virtual3D;

    @Column(name = "review", columnDefinition = "TEXT")
    private String review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "sceneSpots", "category", "children", "parent", "costumes"})
    private Region region;
}