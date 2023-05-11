package com.onix.worldtour.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "Region")
@Table(name = "region")
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class Region {
    @Id
    @SequenceGenerator(name = "region_sequence", sequenceName = "region_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "region_sequence")
    @Column(name = "id", updatable = false)
    private Integer id;

    @Column(name = "name", nullable = false, columnDefinition = "TEXT")
    private String name;

    @Column(name = "commonName", nullable = false, columnDefinition = "TEXT")
    private String commonName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "picture", nullable = false, columnDefinition = "TEXT")
    private String picture;

    @ElementCollection
    @CollectionTable(name = "background", joinColumns = @JoinColumn(name = "region_id"))
    @Column(name = "background")
    private List<String> backgrounds;

    @Embedded
    private Coordinate coordinate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @JsonIgnoreProperties("regions")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "children", "category"})
    private Region parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Region> children = new ArrayList<>();

    @Column(name = "review", columnDefinition = "TEXT")
    private String review;

    @Column(name = "population", columnDefinition = "BIGINT")
    private Long population;

    @Column(name = "area", columnDefinition = "DOUBLE PRECISION")
    private Double area;

    @OneToOne(mappedBy = "region", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, optional = true)
    @JsonIgnoreProperties("region")
    private Country country;

    @OneToMany(mappedBy = "region", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Costume> costumes;

    @OneToMany(mappedBy = "region", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SceneSpot> sceneSpots;

    public void addChild(Region child) {
        children.add(child);
        child.setParent(this);
    }

    public void removeChild(Region child) {
        children.remove(child);
        child.setParent(null);
    }
}
