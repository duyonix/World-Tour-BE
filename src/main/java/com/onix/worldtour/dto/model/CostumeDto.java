package com.onix.worldtour.dto.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.onix.worldtour.model.CostumeType;
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
public class CostumeDto {
    private Integer id;

    private String name;

    private String description;

    private String picture;

    private String model;

    private CostumeType type;

    private Integer regionId;

    private RegionDto region;
}