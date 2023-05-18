package com.onix.worldtour.service;

import com.onix.worldtour.controller.request.RegionRequest;
import com.onix.worldtour.dto.mapper.CountryMapper;
import com.onix.worldtour.dto.mapper.RegionMapper;
import com.onix.worldtour.dto.model.RegionDto;
import com.onix.worldtour.exception.ApplicationException;
import com.onix.worldtour.exception.EntityType;
import com.onix.worldtour.exception.ExceptionType;
import com.onix.worldtour.model.Category;
import com.onix.worldtour.model.Country;
import com.onix.worldtour.model.Region;
import com.onix.worldtour.repository.CategoryRepository;
import com.onix.worldtour.repository.CountryRepository;
import com.onix.worldtour.repository.RegionRepository;
import com.onix.worldtour.repository.SceneSpotRepository;
import com.onix.worldtour.util.ValueMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Transactional
    public RegionDto addRegion(RegionRequest regionRequest) {
        log.info("RegionService::addRegion execution started");
        RegionDto regionDto;

        regionRepository.findByName(regionRequest.getName()).ifPresent(region -> {
            log.error("RegionService::addRegion execution failed with duplicate region name {}", regionRequest.getName());
            throw exception(EntityType.REGION, ExceptionType.DUPLICATE_ENTITY, regionRequest.getName());
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

            regionDto = RegionMapper.toRegionDto(savedRegion);
            log.debug("RegionService::addRegion received response from database {}", ValueMapper.jsonAsString(regionDto));
        } catch (Exception e) {
            log.error("RegionService::addRegion execution failed with error {}", e.getMessage());
            throw exception(EntityType.REGION, ExceptionType.ENTITY_EXCEPTION, e.getMessage());
        }

        log.info("RegionService::addRegion execution completed");
        return regionDto;
    }

    public Page<RegionDto> getRegions(Integer page, Integer size, String search, Integer categoryId) {
        log.info("RegionService::getRegions execution started");
        Page<RegionDto> regionDtos;
        try {
            log.debug("RegionService::getRegions request parameters page {}, size {}, search {}, categoryId {}", page, size, search, categoryId);
            Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
            Page<Region> regions = regionRepository.findByNameContainingAndCategoryId(search, categoryId, pageable);

            regionDtos = regions.map(RegionMapper::toRegionDtoForPage);
            log.debug("RegionService::getRegions received response from database {}", ValueMapper.jsonAsString(regionDtos));
        } catch (Exception e) {
            log.error("RegionService::getRegions execution failed with error {}", e.getMessage());
            throw exception(EntityType.REGION, ExceptionType.ENTITY_EXCEPTION, e.getMessage());
        }

        log.info("RegionService::getRegions execution completed");
        return regionDtos;
    }

    public List<RegionDto> getRegionOptions(String search) {
        log.info("RegionService::getRegionOptions execution started");
        List<RegionDto> regionDtos;
        try {
            log.debug("RegionService::getRegionOptions request parameters search {}", search);
            List<Region> regions = regionRepository.findByNameContaining(search);

            regionDtos = regions.stream().map(RegionMapper::toRegionDtoForPage).toList();
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
        log.debug("RegionService::getRegion received response from database {}", ValueMapper.jsonAsString(regionDto));

        log.info("RegionService::getRegion execution completed");
        return regionDto;
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

        Region duplicateRegion = regionRepository.findByName(regionRequest.getName()).orElse(null);
        if (duplicateRegion != null && !duplicateRegion.getId().equals(id)) {
            log.error("RegionService::updateRegion execution failed with duplicate region name {}", regionRequest.getName());
            throw exception(EntityType.REGION, ExceptionType.DUPLICATE_ENTITY, regionRequest.getName());
        }

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

            if (country != null) {
                country.setRegion(savedRegion);
                Country savedCountry = countryRepository.save(country);
                savedRegion.setCountry(savedCountry);
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

        if (!region.getCostumes().isEmpty()) {
            log.error("RegionService::deleteRegion execution failed with region is use");
            throw exception(EntityType.REGION, ExceptionType.ALREADY_USED_ELSEWHERE, id.toString());
        }

        try {
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

    private RuntimeException exception(EntityType entityType, ExceptionType exceptionType, String... args) {
        return ApplicationException.throwException(entityType, exceptionType, args);
    }
}
