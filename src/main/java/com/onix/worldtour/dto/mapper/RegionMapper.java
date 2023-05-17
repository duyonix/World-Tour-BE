package com.onix.worldtour.dto.mapper;

import com.onix.worldtour.controller.request.RegionRequest;
import com.onix.worldtour.dto.model.RegionDto;
import com.onix.worldtour.model.Category;
import com.onix.worldtour.model.Country;
import com.onix.worldtour.model.Region;

import java.util.stream.Collectors;

public class RegionMapper {
    public static Region toRegion(RegionRequest regionRequest) {
        return new Region()
                .setName(regionRequest.getName())
                .setCommonName(regionRequest.getCommonName())
                .setDescription(regionRequest.getDescription())
                .setPicture(regionRequest.getPicture())
                .setBackgrounds(regionRequest.getBackgrounds())
                .setCoordinate(regionRequest.getCoordinate())
                .setPopulation(regionRequest.getPopulation())
                .setArea(regionRequest.getArea())
                .setReview(regionRequest.getReview());
    }

    public static RegionDto toRegionDto(Region region) {
        Region parent = region.getParent() != null ? region.getParent() : null;
        Country country = region.getCountry() != null ? region.getCountry() : null;

        RegionDto regionDto = new RegionDto()
                .setId(region.getId())
                .setName(region.getName())
                .setCommonName(region.getCommonName())
                .setDescription(region.getDescription())
                .setPicture(region.getPicture())
                .setBackgrounds(region.getBackgrounds())
                .setCategoryId(region.getCategory().getId())
                .setCategory(CategoryMapper.toCategoryOptionDto(region.getCategory()))
                .setCoordinate(region.getCoordinate())
                .setParentId(parent != null ? parent.getId() : null)
                .setParent(parent != null ? RegionMapper.toRegionOptionDto(parent) : null)
                .setPopulation(region.getPopulation())
                .setArea(region.getArea())
                .setReview(region.getReview())
                .setCountryId(country != null ? country.getId() : null)
                .setCountry(country != null ? CountryMapper.toCountryDto(country) : null);

        if (region.getCostumes() != null) {
            regionDto.setCostumes(region.getCostumes().stream()
                    .map(CostumeMapper::toCostumeDtoForRegion)
                    .collect(Collectors.toList()));
        }

        return regionDto;
    }

    public static RegionDto toRegionDtoForPage(Region region) {
        Region parent = region.getParent() != null ? region.getParent() : null;
        return new RegionDto()
                .setId(region.getId())
                .setName(region.getName())
                .setCommonName(region.getCommonName())
                .setDescription(region.getDescription())
                .setPicture(region.getPicture())
                .setCategoryId(region.getCategory().getId())
                .setCategory(CategoryMapper.toCategoryOptionDto(region.getCategory()))
                .setCoordinate(region.getCoordinate())
                .setParentId(parent != null ? parent.getId() : null)
                .setParent(parent != null ? RegionMapper.toRegionOptionDto(parent) : null);
    }

    public static RegionDto toRegionOptionDto(Region region) {
        return new RegionDto()
                .setId(region.getId())
                .setName(region.getName());
    }
}