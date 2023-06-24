package com.onix.worldtour.dto.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.onix.worldtour.model.Coordinate;
import com.onix.worldtour.model.Country;
import com.onix.worldtour.model.PanoramaType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegionDto {
    private Integer id;

    private String name;

    private String commonName;

    private String description;

    private String picture;

    private List<String> backgrounds;

    private Coordinate coordinate;

    private Integer categoryId;

    private CategoryDto category;

    private Integer parentId;

    private RegionDto parent;

    private String review;

    private Object reviewInfo;

    private Long population;

    private Double area;

    private String panorama;

    private PanoramaType panoramaType;

    private String model;

    private Integer countryId;

    private CountryDto country;

    private List<SceneSpotDto> sceneSpots;

    private List<CostumeDto> costumes;

    private WeatherDto weather;

    private List<RegionDto> neighbors;

    private String path;

    private Boolean hasChildren;

    private Boolean hasStreetView;
}