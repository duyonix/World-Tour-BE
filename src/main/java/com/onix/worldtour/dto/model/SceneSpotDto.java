package com.onix.worldtour.dto.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.onix.worldtour.model.PanoramaType;
import com.onix.worldtour.model.Virtual3D;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SceneSpotDto {
    private Integer id;

    private String name;

    private String description;

    private String picture;

    private String panorama;

    private PanoramaType panoramaType;

    private Virtual3D virtual3D;

    private String review;

    private Object reviewInfo;
}
