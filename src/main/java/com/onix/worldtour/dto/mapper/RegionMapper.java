package com.onix.worldtour.dto.mapper;

import com.onix.worldtour.controller.request.CountryRestData;
import com.onix.worldtour.controller.request.RegionRequest;
import com.onix.worldtour.dto.model.RegionDto;
import com.onix.worldtour.model.Category;
import com.onix.worldtour.model.Coordinate;
import com.onix.worldtour.model.Country;
import com.onix.worldtour.model.Region;

import java.util.List;
import java.util.stream.Collectors;

public class RegionMapper {
    public static RegionRequest toRegionRequestForCountry(CountryRestData data, List<Region> parentRegions) {
        String defaultBackground = "https://storage.googleapis.com/onix-world-tour.appspot.com/dev/background/DefaultBackground.jpg";
        return new RegionRequest()
                .setName(data.getName().getOfficial())
                .setCommonName(data.getName().getCommon())
                .setPicture(data.getFlags().getPng())
                .setBackgrounds(List.of(defaultBackground))
                .setCoordinate(new Coordinate(Double.parseDouble(data.getLatlng()[0]), Double.parseDouble(data.getLatlng()[1])))
                .setCategoryId(4)
                .setDescription("")
                .setReview("")
                .setPopulation(data.getPopulation())
                .setArea(data.getArea())
                .setCountry(CountryMapper.toCountryRequest(data))
                .setParentId(getParendId(data.getSubregion(), parentRegions));
    }

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

    private static Integer getParendId(String subregion, List<Region> parentRegions) {
        if (subregion == null) {
            subregion = "Antarctica";
        }

        for (Region region : parentRegions) {
            if (region.getCommonName().equals(subregion)) {
                return region.getId();
            }
        }

        return null;
    }
}