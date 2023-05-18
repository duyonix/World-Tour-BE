package com.onix.worldtour.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity(name = "Costume")
@Table(name = "costume")
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class Costume {
    @Id
    @SequenceGenerator(name = "costume_sequence", sequenceName = "costume_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "costume_sequence")
    @Column(name = "id", updatable = false)
    private Integer id;

    @Column(name = "name", nullable = false, columnDefinition = "TEXT")
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "picture", nullable = false, columnDefinition = "TEXT")
    private String picture;

    @Column(name = "model", nullable = false, columnDefinition = "TEXT")
    private String model;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private CostumeType type = CostumeType.COMMON;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "costumes", "category", "children", "parent", "sceneSpots"})
    private Region region;
}