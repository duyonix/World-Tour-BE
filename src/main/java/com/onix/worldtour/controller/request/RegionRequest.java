package com.onix.worldtour.controller.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.onix.worldtour.model.Coordinate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class RegionRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Common name is required")
    private String commonName;

    private String description;

    @NotBlank(message = "Picture is required")
    private String picture;

    @NotEmpty(message = "Backgrounds is required")
    private List<String> backgrounds;

    @NotNull(message = "Coordinate is required")
    private Coordinate coordinate;

    @NotNull(message = "Category ID is required")
    private Integer categoryId;

    private Integer parentId;

    private String review;

    private Long population;

    private Double area;

    private CountryRequest country;

    private List<SceneSpotRequest> sceneSpots;
}
