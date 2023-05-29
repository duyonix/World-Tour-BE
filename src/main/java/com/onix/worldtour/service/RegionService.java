package com.onix.worldtour.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onix.worldtour.controller.data.CountryNameData;
import com.onix.worldtour.controller.data.CountryRestData;
import com.onix.worldtour.controller.request.RegionRequest;
import com.onix.worldtour.controller.data.StateData;
import com.onix.worldtour.dto.mapper.CountryMapper;
import com.onix.worldtour.dto.mapper.RegionMapper;
import com.onix.worldtour.dto.mapper.SceneSpotMapper;
import com.onix.worldtour.dto.model.RegionDto;
import com.onix.worldtour.dto.model.WeatherDto;
import com.onix.worldtour.exception.ApplicationException;
import com.onix.worldtour.exception.EntityType;
import com.onix.worldtour.exception.ExceptionType;
import com.onix.worldtour.model.*;
import com.onix.worldtour.repository.CategoryRepository;
import com.onix.worldtour.repository.CountryRepository;
import com.onix.worldtour.repository.RegionRepository;
import com.onix.worldtour.repository.SceneSpotRepository;
import com.onix.worldtour.util.Util;
import com.onix.worldtour.util.ValueMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RegionService {
    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private SceneSpotRepository sceneSpotRepository;

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private ReviewService reviewService;

    @Transactional
    public RegionDto addRegion(RegionRequest regionRequest) {
        log.info("RegionService::addRegion execution started");
        RegionDto regionDto;

        regionRepository.findByNameOrCommonNameAndCategoryIdAndParentId(regionRequest.getName(), regionRequest.getCommonName(), regionRequest.getCategoryId(), regionRequest.getParentId()).ifPresent(region -> {
            log.error("RegionService::addRegion execution failed with region already exists {}", regionRequest.getName());
            throw exception(EntityType.REGION, ExceptionType.DUPLICATE_ENTITY, regionRequest.getName() + " or " + regionRequest.getCommonName());
        });

        Category category = categoryRepository.findById(regionRequest.getCategoryId()).orElseThrow(() -> {
            log.error("RegionService::addRegion execution failed with category not found {}", regionRequest.getCategoryId());
            throw exception(EntityType.CATEGORY, ExceptionType.ENTITY_NOT_FOUND, regionRequest.getCategoryId().toString());
        });

        Region parent = null;
        if (regionRequest.getParentId() != null) {
            parent = regionRepository.findById(regionRequest.getParentId()).orElseThrow(() -> {
                log.error("RegionService::addRegion execution failed with parent region not found {}", regionRequest.getParentId());
                throw exception(EntityType.PARENT_REGION, ExceptionType.ENTITY_NOT_FOUND, regionRequest.getParentId().toString());
            });
        }
        checkParentIsSuitable(parent, category);

        if (category.getLevel() == 4 && regionRequest.getCountry() == null) {
            log.error("RegionService::addRegion execution failed with invalid country for category level Country");
            throw exception(EntityType.COUNTRY, ExceptionType.ENTITY_NOT_FOUND, category.getId().toString());
        }
        Country country = category.getLevel() == 4 && regionRequest.getCountry() != null ? CountryMapper.toCountry(regionRequest.getCountry()) : null;

        try {
            Region newRegion = RegionMapper.toRegion(regionRequest)
                    .setCategory(category)
                    .setParent(parent);
            log.debug("RegionService::addRegion request parameters {}", ValueMapper.jsonAsString(newRegion));

            Region savedRegion = regionRepository.save(newRegion);

            if (country != null) {
                country.setRegion(savedRegion);
                Country savedCountry = countryRepository.save(country);
                savedRegion.setCountry(savedCountry);
            }

            if(regionRequest.getSceneSpots() != null && !regionRequest.getSceneSpots().isEmpty()) {
                List<SceneSpot> sceneSpots = regionRequest.getSceneSpots().stream().map(SceneSpotMapper::toSceneSpot).toList();
                sceneSpots.forEach(sceneSpot -> sceneSpot.setRegion(savedRegion));
                List<SceneSpot> savedSceneSpots = sceneSpotRepository.saveAll(sceneSpots);
                savedRegion.setSceneSpots(savedSceneSpots);
            }

            regionDto = RegionMapper.toRegionDto(savedRegion);
            log.debug("RegionService::addRegion received response from database {}", ValueMapper.jsonAsString(regionDto));
        } catch (Exception e) {
            log.error("RegionService::addRegion execution failed with error {}", e.getMessage());
            throw exception(EntityType.REGION, ExceptionType.ENTITY_EXCEPTION, e.getMessage());
        }

        log.info("RegionService::addRegion execution completed");
        return regionDto;
    }

    public Page<RegionDto> getRegions(Integer page, Integer size, String search, Integer categoryId, Integer parentId) {
        log.info("RegionService::getRegions execution started");
        Page<RegionDto> regionDtos;
        try {
            log.debug("RegionService::getRegions request parameters page {}, size {}, search {}, categoryId {}, parentId {}", page, size, search, categoryId, parentId);
            Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
            Page<Region> regions = regionRepository.findBySearchAndCategoryIdAndParentId(search, categoryId, parentId, pageable);

            regionDtos = regions.map(RegionMapper::toRegionDtoForPage);
            mapListRegionDtoWithPath(regionDtos.getContent(), regions.getContent());
            log.debug("RegionService::getRegions received response from database {}", ValueMapper.jsonAsString(regionDtos));
        } catch (Exception e) {
            log.error("RegionService::getRegions execution failed with error {}", e.getMessage());
            throw exception(EntityType.REGION, ExceptionType.ENTITY_EXCEPTION, e.getMessage());
        }

        log.info("RegionService::getRegions execution completed");
        return regionDtos;
    }

    public List<RegionDto> getRegionOptions(String search, Integer level, Double lattitude, Double longitude, Double distance) {
        log.info("RegionService::getRegionOptions execution started");
        List<RegionDto> regionDtos;
        try {
            log.debug("RegionService::getRegionOptions request parameters search {}, level {}, lattitude {}, longitude {}, distance {}", search, level, lattitude, longitude, distance);
            List<Region> regions;
            if(lattitude != null && longitude != null && distance != null) {
                double lattitudeDelta = Util.calculateLatitudeDelta(distance);
                double longitudeDelta = Util.calculateLongitudeDelta(distance, lattitude);

                double minLattitude = lattitude - lattitudeDelta;
                double maxLattitude = lattitude + lattitudeDelta;
                double minLongitude = longitude - longitudeDelta;
                double maxLongitude = longitude + longitudeDelta;

                regions = regionRepository.findBySearchAndCategoryLevelAndWithinBounds(search, level, minLattitude, maxLattitude, minLongitude, maxLongitude);
            } else {
                regions = regionRepository.findBySearchAndCategoryLevel(search, level);
            }

            regionDtos = regions.stream().map(RegionMapper::toRegionDtoForPage).toList();

            List<Region> finalRegions = regions;
            mapListRegionDtoWithPath(regionDtos, finalRegions);
            log.debug("RegionService::getRegionOptions received response from database {}", ValueMapper.jsonAsString(regionDtos));
        } catch (Exception e) {
            log.error("RegionService::getRegionOptions execution failed with error {}", e.getMessage());
            throw exception(EntityType.REGION, ExceptionType.ENTITY_EXCEPTION, e.getMessage());
        }

        log.info("RegionService::getRegionOptions execution completed");
        return regionDtos;
    }

    public RegionDto getRegion(Integer id) {
        log.info("RegionService::getRegion execution started");
        RegionDto regionDto;

        log.debug("RegionService::getRegion request parameters id {}", id);
        Region region = regionRepository.findById(id).orElseThrow(() -> {
            log.error("RegionService::getRegion execution failed with invalid region id {}", id);
            return exception(EntityType.REGION, ExceptionType.ENTITY_NOT_FOUND, id.toString());
        });
        regionDto = RegionMapper.toRegionDto(region);

        // region has category level >=4 then get weather for it
        if(region.getCategory().getLevel() >= 4) {
            WeatherDto weather = weatherService.getWeather(region.getCoordinate());
            regionDto.setWeather(weather);
        }

        // get infoReview for region
        if(region.getReview() != null) {
            Object infoReview = reviewService.getVideoData(region.getReview());
            regionDto.setReviewInfo(infoReview);
        }

        // has children
        List<Region> childrenRegions = regionRepository.findByParentId(region.getId());
        regionDto.setHasChildren(!childrenRegions.isEmpty());

        // get 3 nearest neighboring regions
        List<Region> neighborRegions = new ArrayList<>();
        if(region.getCategory().getLevel() != 1 && region.getParent() != null) {
            neighborRegions = regionRepository.findByCategoryIdAndParentId(region.getCategory().getId(), region.getParent().getId());
        }
        List<RegionDto> neighborRegionDtos = neighborRegions.stream()
                .filter(neighborRegion -> !neighborRegion.getId().equals(region.getId()))
                .sorted(Comparator.comparing(neighborRegion -> Util.getDistance(region.getCoordinate(), neighborRegion.getCoordinate())))
                .map(RegionMapper::toRegionOptionDto)
                .limit(3)
                .toList();
        regionDto.setNeighbors(neighborRegionDtos);
        regionDto.setPath(getRegionPath(region));
        log.debug("RegionService::getRegion received response from database {}", ValueMapper.jsonAsString(regionDto));

        log.info("RegionService::getRegion execution completed");
        return regionDto;
    }

    @Transactional
    public void importCountries() {
        log.info("RegionService::importCountries execution started");
        String apiUrl = "https://restcountries.com/v3.1/all";
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<CountryRestData[]> response = restTemplate.getForEntity(apiUrl, CountryRestData[].class);
            CountryRestData[] countryDataArray = response.getBody();

            // get List of Regions has category level 3: Region
            List<Region> parentRegions = regionRepository.findByCategoryLevel(3);

            if (countryDataArray != null) {
                for (CountryRestData countryData : countryDataArray) {
                    RegionRequest regionRequest = RegionMapper.toRegionRequestForCountry(countryData, parentRegions);
                    addRegion(regionRequest);
                }
            }
        } catch (Exception e) {
            log.error("RegionService::importCountries execution failed with error {}", e.getMessage());
            throw exception(EntityType.REGION, ExceptionType.ENTITY_EXCEPTION, e.getMessage());
        }
    }

    @Transactional
    public void updateCountryName() {
        // update from countries-name.json
        log.info("RegionService::updateCountryName execution started");
        try {
            ClassPathResource resource = new ClassPathResource("countries-name.json");
            ObjectMapper objectMapper = new ObjectMapper();
            CountryNameData[] countryNameDataArray = objectMapper.readValue(resource.getInputStream(), CountryNameData[].class);

            for (CountryNameData countryNameData : countryNameDataArray) {
                Region region = regionRepository.findByCountryCode(countryNameData.getCode());
                if(region != null) {
                    region.setName(countryNameData.getName());
                    regionRepository.save(region);
                }
            }
        } catch (Exception e) {
            log.error("RegionService::updateCountryName execution failed with error {}", e.getMessage());
            throw exception(EntityType.REGION, ExceptionType.ENTITY_EXCEPTION, e.getMessage());
        }
    }

    @Transactional
    public void importStates() {
        // import from states.json
        log.info("RegionService::importStates execution started");
        try {
            ClassPathResource resource = new ClassPathResource("states.json");
            ObjectMapper objectMapper = new ObjectMapper();
            StateData[] stateDataArray = objectMapper.readValue(resource.getInputStream(), StateData[].class);
            List<Region> countries = regionRepository.findByCategoryLevel(4); // get List of Regions has category level 4: Country

            Map<String, Integer> countryMapper = countries.stream()
                    .collect(Collectors.toMap(country -> country.getCountry().getCode(), Region::getId));
            for (StateData stateData : stateDataArray) {
                if (stateData.getCountry_code() != null && stateData.getName() != null &&
                        stateData.getLatitude() != null && stateData.getLongitude() != null) {
                    RegionRequest regionRequest = RegionMapper.toRegionRequestForState(stateData, countryMapper);
                    addRegion(regionRequest);
                }
            }

        } catch (Exception e) {
            log.error("RegionService::importStates execution failed with error {}", e.getMessage());
            throw exception(EntityType.REGION, ExceptionType.ENTITY_EXCEPTION, e.getMessage());
        }
    }

    @Transactional
    public RegionDto updateRegion(Integer id, RegionRequest regionRequest) {
        log.info("RegionService::updateRegion execution started");
        RegionDto regionDto;

        log.debug("RegionService::updateRegion request parameters id {}, regionRequest {}", id, ValueMapper.jsonAsString(regionRequest));
        Region region = regionRepository.findById(id).orElseThrow(() -> {
            log.error("RegionService::updateRegion execution failed with invalid region id {}", id);
            throw exception(EntityType.REGION, ExceptionType.ENTITY_NOT_FOUND, id.toString());
        });

        regionRepository.findByNameOrCommonNameAndCategoryIdAndParentId(regionRequest.getName(), regionRequest.getCommonName(), regionRequest.getCategoryId(), regionRequest.getParentId())
                .ifPresent(duplicateRegion -> {
                    if(!duplicateRegion.getId().equals(id)) {
                        log.error("RegionService::updateRegion execution failed with duplicate region name {} or common name {} for category {}", regionRequest.getName(), regionRequest.getCommonName(), regionRequest.getCategoryId());
                        throw exception(EntityType.REGION, ExceptionType.DUPLICATE_ENTITY, regionRequest.getName() + " or " + regionRequest.getCommonName());
                    }
                });

        Category category = categoryRepository.findById(regionRequest.getCategoryId()).orElseThrow(() -> {
            log.error("RegionService::updateRegion execution failed with category not found {}", regionRequest.getCategoryId());
            throw exception(EntityType.CATEGORY, ExceptionType.ENTITY_NOT_FOUND, regionRequest.getCategoryId().toString());
        });

        Region parent = null;
        if (regionRequest.getParentId() != null) {
            parent = regionRepository.findById(regionRequest.getParentId()).orElseThrow(() -> {
                log.error("RegionService::updateRegion execution failed with parent region not found {}", regionRequest.getParentId());
                throw exception(EntityType.PARENT_REGION, ExceptionType.ENTITY_NOT_FOUND, regionRequest.getParentId().toString());
            });
        }
        checkParentIsSuitable(parent, category);

        if (category.getLevel() == 4 && regionRequest.getCountry() == null) {
            log.error("RegionService::updateRegion execution failed with invalid country for category level Country");
            throw exception(EntityType.COUNTRY, ExceptionType.ENTITY_NOT_FOUND, category.getId().toString());
        }
        Country country = category.getLevel() == 4 && regionRequest.getCountry() != null ? CountryMapper.toCountry(regionRequest.getCountry()) : null;

        try {
            Region updatedRegion = RegionMapper.toRegion(regionRequest)
                    .setId(id)
                    .setCategory(category)
                    .setParent(parent)
                    .setCostumes(region.getCostumes())
                    .setSceneSpots(region.getSceneSpots());
            log.debug("RegionService::updateRegion saving region to database {}", ValueMapper.jsonAsString(updatedRegion));
            Region savedRegion = regionRepository.save(updatedRegion);

            //  update Country in Region
            if (country != null) {
                country.setRegion(savedRegion);
                Country savedCountry = countryRepository.save(country);
                savedRegion.setCountry(savedCountry);
            }

            // update SceneSpots in Region
            List<SceneSpot> beforeSceneSpots = region.getSceneSpots() != null ? region.getSceneSpots() : new ArrayList<>();
            List<SceneSpot> addSceneSpots = regionRequest.getSceneSpots().stream().filter(sceneSpotRequest -> sceneSpotRequest.getId() == null).map(SceneSpotMapper::toSceneSpot).toList();
            List<SceneSpot> updateSceneSpots = regionRequest.getSceneSpots().stream().filter(sceneSpotRequest -> sceneSpotRequest.getId() != null).map(SceneSpotMapper::toSceneSpot).toList();
            List<SceneSpot> deleteSceneSpots = beforeSceneSpots.stream().filter(beforeSceneSpot -> updateSceneSpots.stream().noneMatch(updateSceneSpot -> updateSceneSpot.getId().equals(beforeSceneSpot.getId()))).toList();

            // add new SceneSpots
            if (!addSceneSpots.isEmpty()) {
                addSceneSpots.forEach(sceneSpot -> sceneSpot.setRegion(savedRegion));
                List<SceneSpot> savedSceneSpots = sceneSpotRepository.saveAll(addSceneSpots);
                savedRegion.getSceneSpots().addAll(savedSceneSpots);
            }

            // update SceneSpots
            for (SceneSpot updateSceneSpot : updateSceneSpots) {
                SceneSpot beforeSceneSpot = beforeSceneSpots.stream().filter(sceneSpot -> sceneSpot.getId().equals(updateSceneSpot.getId())).findFirst().orElseThrow(() -> {
                    log.error("RegionService::updateRegion execution failed with scene spot not found {}", updateSceneSpot.getId());
                    throw exception(EntityType.SCENE_SPOT, ExceptionType.ENTITY_NOT_FOUND, updateSceneSpot.getId().toString());
                });
                updateSceneSpot.setRegion(savedRegion);
                SceneSpot savedSceneSpot = sceneSpotRepository.save(updateSceneSpot);
                savedRegion.getSceneSpots().remove(beforeSceneSpot);
                savedRegion.getSceneSpots().add(savedSceneSpot);
            }

            // delete SceneSpots
            if (!deleteSceneSpots.isEmpty()) {
                sceneSpotRepository.deleteAll(deleteSceneSpots);
                savedRegion.getSceneSpots().removeAll(deleteSceneSpots);
            }

            regionDto = RegionMapper.toRegionDto(savedRegion);
            log.debug("RegionService::updateRegion received response from database {}", ValueMapper.jsonAsString(regionDto));
        } catch (Exception e) {
            log.error("RegionService::updateRegion execution failed with error {}", e.getMessage());
            throw exception(EntityType.REGION, ExceptionType.ENTITY_EXCEPTION, e.getMessage());
        }

        log.info("RegionService::updateRegion execution completed");
        return regionDto;
    }

    @Transactional
    public RegionDto deleteRegion(Integer id) {
        log.info("RegionService::deleteRegion execution started");
        RegionDto regionDto;

        log.debug("RegionService::deleteRegion request parameters id {}", id);
        Region region = regionRepository.findById(id).orElseThrow(() -> {
            log.error("RegionService::deleteRegion execution failed with invalid region id {}", id);
            throw exception(EntityType.REGION, ExceptionType.ENTITY_NOT_FOUND, id.toString());
        });

        if (!region.getCostumes().isEmpty() || !region.getChildren().isEmpty()) {
            log.error("RegionService::deleteRegion execution failed with region is use");
            throw exception(EntityType.REGION, ExceptionType.ALREADY_USED_ELSEWHERE, id.toString());
        }

        try {
            if (region.getCountry() != null) {
                countryRepository.delete(region.getCountry());
            }
            if(!region.getSceneSpots().isEmpty()) {
                sceneSpotRepository.deleteAll(region.getSceneSpots());
            }

            regionDto = RegionMapper.toRegionDto(region);
            regionRepository.delete(region);
            log.debug("RegionService::deleteRegion received response from database {}", ValueMapper.jsonAsString(regionDto));
        } catch (Exception e) {
            log.error("RegionService::deleteRegion execution failed with error {}", e.getMessage());
            throw exception(EntityType.REGION, ExceptionType.ENTITY_EXCEPTION, e.getMessage());
        }

        log.info("RegionService::deleteRegion execution completed");
        return regionDto;
    }

    public List<RegionDto> getAncestorRegions(Integer id) {
        log.info("RegionService::getAncestorRegions execution started");
        List<RegionDto> regionDtos;

        log.debug("RegionService::getAncestorRegions request parameters id {}", id);
        Region region = regionRepository.findById(id).orElseThrow(() -> {
            log.error("RegionService::getAncestorRegions execution failed with invalid region id {}", id);
            throw exception(EntityType.REGION, ExceptionType.ENTITY_NOT_FOUND, id.toString());
        });

        try {
            List<Region> regions = new ArrayList<>();
            Region parent = region.getParent();
            while (parent != null) {
                regions.add(parent);
                parent = parent.getParent();
            }

            Collections.reverse(regions);
            regionDtos = regions.stream().map(RegionMapper::toRegionOptionDto).toList();

            log.debug("RegionService::getAncestorRegions received response from database {}", ValueMapper.jsonAsString(regionDtos));
        } catch (Exception e) {
            log.error("RegionService::getAncestorRegions execution failed with error {}", e.getMessage());
            throw exception(EntityType.REGION, ExceptionType.ENTITY_EXCEPTION, e.getMessage());
        }

        log.info("RegionService::getAncestorRegions execution completed");
        return regionDtos;
    }

    private String getRegionPath(Region region) {
        List<String> path = new ArrayList<>();
        path.add(region.getName());
        Region parent = region.getParent();
        while (parent != null) {
            path.add(parent.getName());
            parent = parent.getParent();
        }
        Collections.reverse(path);
        return String.join(" / ", path);
    }

    private void mapListRegionDtoWithPath(List<RegionDto> regionDtos, List<Region> regions) {
        regionDtos.forEach(regionDto -> {
            Region region = regions.stream().filter(r -> r.getId().equals(regionDto.getId())).findFirst().orElse(null);
            if(region != null) {
                String path = getRegionPath(region);
                regionDto.setPath(path);
            }
        });
    }

    private void checkParentIsSuitable(Region parent, Category category) {
        int categoryLevel = category.getLevel();
        boolean isSuitable = (categoryLevel == 1 && parent == null) ||
                (categoryLevel > 1 && parent != null && categoryLevel == parent.getCategory().getLevel() + 1);

        if (!isSuitable) {
            log.error("RegionService::checkParentIsSuitable execution failed with parent is not suitable");
            throw exception(EntityType.PARENT_REGION, ExceptionType.NOT_SUITABLE, parent != null ? parent.getId().toString() : "null");
        }
    }

    private RuntimeException exception(EntityType entityType, ExceptionType exceptionType, String... args) {
        return ApplicationException.throwException(entityType, exceptionType, args);
    }
}
