package com.onix.worldtour.dto.mapper;

import com.onix.worldtour.dto.model.RegionDto;
import com.onix.worldtour.model.Region;

public class RegionMapper {

    public static RegionDto toRegionDtoForCostume(Region region) {
        return new RegionDto()
                .setId(region.getId())
                .setName(region.getName());
    }
}