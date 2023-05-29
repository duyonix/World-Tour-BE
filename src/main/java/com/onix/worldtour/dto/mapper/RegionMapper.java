package com.onix.worldtour.dto.mapper;

import com.onix.worldtour.controller.data.CountryRestData;
import com.onix.worldtour.controller.request.RegionRequest;
import com.onix.worldtour.controller.data.StateData;
import com.onix.worldtour.dto.model.RegionDto;
import com.onix.worldtour.dto.model.SceneSpotDto;
import com.onix.worldtour.model.Coordinate;
import com.onix.worldtour.model.Country;
import com.onix.worldtour.model.Region;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RegionMapper {
    public static String defaultBackground = "https://storage.googleapis.com/onix-world-tour.appspot.com/dev/background/DefaultBackground.jpg";
    public static String defaultPicture = "https://storage.googleapis.com/onix-world-tour.appspot.com/dev/picture/city.png";

    public static RegionRequest toRegionRequestForCountry(CountryRestData data, List<Region> parentRegions) {
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
                .setParentId(getParentIdForCountry(data.getSubregion(), parentRegions));
    }

    public static RegionRequest toRegionRequestForState(StateData data, Map<String, Integer> countryMapper) {
        return new RegionRequest()
                .setName(data.getName())
                .setCommonName(data.getName())
                .setPicture(defaultPicture)
                .setBackgrounds(List.of(defaultBackground))
                .setCoordinate(new Coordinate(Double.parseDouble(data.getLatitude()), Double.parseDouble(data.getLongitude())))
                .setCategoryId(5)
                .setParentId(countryMapper.get(data.getCountry_code()) != null ? countryMapper.get(data.getCountry_code()) : 211);
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

        if(region.getSceneSpots() != null && !region.getSceneSpots().isEmpty()) {
            List<SceneSpotDto> sceneSpotDtos = region.getSceneSpots().stream()
                    .map(SceneSpotMapper::toSceneSpotDto)
                    .collect(Collectors.toList());
            regionDto.setSceneSpots(sceneSpotDtos);
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
                .setName(region.getName())
                .setCommonName(region.getCommonName())
                .setPicture(region.getPicture());
    }

    private static Integer getParentIdForCountry(String subregion, List<Region> parentRegions) {
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