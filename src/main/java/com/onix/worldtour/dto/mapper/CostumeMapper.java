package com.onix.worldtour.dto.mapper;

import com.onix.worldtour.controller.request.CostumeRequest;
import com.onix.worldtour.dto.model.CostumeDto;
import com.onix.worldtour.model.Costume;
import com.onix.worldtour.model.Region;

public class CostumeMapper {
    public static Costume toCostume(CostumeRequest costumeRequest, Region region) {
        return new Costume()
                .setName(costumeRequest.getName())
                .setDescription(costumeRequest.getDescription())
                .setPicture(costumeRequest.getPicture())
                .setModel(costumeRequest.getModel())
                .setType(costumeRequest.getType())
                .setRegion(region);
    }

    public static CostumeDto toCostumeDto(Costume costume) {
        return new CostumeDto()
                .setId(costume.getId())
                .setName(costume.getName())
                .setDescription(costume.getDescription())
                .setPicture(costume.getPicture())
                .setModel(costume.getModel())
                .setType(costume.getType())
                .setRegionId(costume.getRegion().getId())
                .setRegion(RegionMapper.toRegionDtoForCostume(costume.getRegion()));
    }

    public static CostumeDto toCostumeDtoForRegion(Costume costume) {
        return new CostumeDto()
                .setId(costume.getId())
                .setName(costume.getName())
                .setDescription(costume.getDescription())
                .setPicture(costume.getPicture())
                .setModel(costume.getModel())
                .setType(costume.getType())
                .setRegionId(costume.getRegion().getId());
    }
}
