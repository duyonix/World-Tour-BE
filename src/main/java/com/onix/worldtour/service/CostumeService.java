package com.onix.worldtour.service;

import com.onix.worldtour.controller.request.CostumeRequest;
import com.onix.worldtour.dto.mapper.CostumeMapper;
import com.onix.worldtour.dto.model.CostumeDto;
import com.onix.worldtour.exception.ApplicationException;
import com.onix.worldtour.exception.EntityType;
import com.onix.worldtour.exception.ExceptionType;
import com.onix.worldtour.model.Costume;
import com.onix.worldtour.model.CostumeType;
import com.onix.worldtour.model.Region;
import com.onix.worldtour.repository.CostumeRepository;
import com.onix.worldtour.repository.RegionRepository;
import com.onix.worldtour.util.ValueMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CostumeService {
    @Autowired
    private CostumeRepository costumeRepository;

    @Autowired
    private RegionRepository regionRepository;

    public CostumeDto addCostume(CostumeRequest costumeRequest) {
        log.info("CostumeService::addCostume execution started");
        CostumeDto costumeDto;

        costumeRepository.findByName(costumeRequest.getName())
                .ifPresent(costume -> {
                    log.error("CostumeService::addCostume execution failed with duplicate costume name {}", costumeRequest.getName());
                    throw exception(EntityType.COSTUME, ExceptionType.DUPLICATE_ENTITY, costumeRequest.getName());
                });

        Region region = null;
        boolean isSpecific = costumeRequest.getType().equals(CostumeType.SPECIFIC);
        if (isSpecific) {
            Integer regionId = costumeRequest.getRegionId();
            if(regionId == null) {
                log.error("CostumeService::addCostume execution failed for specific type with null region id");
                throw exception(EntityType.REGION, ExceptionType.ENTITY_NOT_FOUND, "null");
            }

            region = regionRepository.findById(regionId)
                    .orElseThrow(() -> {
                        log.error("CostumeService::addCostume execution failed for specific type with invalid region id {}", regionId);
                        throw exception(EntityType.REGION, ExceptionType.ENTITY_NOT_FOUND, regionId.toString());
                    });
        }

        try {
            Region applyRegion = isSpecific ? region : null;
            Costume newCostume = CostumeMapper.toCostume(costumeRequest, applyRegion);
            log.debug("CostumeService::addCostume request parameters {}", ValueMapper.jsonAsString(newCostume));

            Costume savedCostume = costumeRepository.save(newCostume);
            costumeDto = CostumeMapper.toCostumeDto(savedCostume);
            log.debug("CostumeService::addCostume received response from database {}", ValueMapper.jsonAsString(costumeDto));
        } catch (Exception e) {
            log.error("CostumeService::addCostume execution failed with error {}", e.getMessage());
            throw exception(EntityType.COSTUME, ExceptionType.ENTITY_EXCEPTION, e.getMessage());
        }

        log.info("CostumeService::addCostume execution completed");
        return costumeDto;
    }

    public Page<CostumeDto> getCostumes(Integer page, Integer size, String search, Integer regionId, String type) {
        log.info("CostumeService::getCostumes execution started");
        Page<CostumeDto> costumeDtos;

        try {
            log.debug("CostumeService::getCostumes request parameters page {}, size {}, search {}, regionId {} type {}", page, size, search, regionId, type);
            Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
            CostumeType costumeType = type != null ? CostumeType.valueOf(type.toUpperCase()) : null;
            Page<Costume> costumes = costumeRepository.findByNameContainingAndRegionIdAndType(search, regionId, costumeType, pageable);
            log.info("CostumeService::getCostumes received response from database costumes {}", ValueMapper.jsonAsString(costumes));

            costumeDtos = costumes.map(CostumeMapper::toCostumeDto);
            log.info("CostumeService::getCostumes received response from database {}", ValueMapper.jsonAsString(costumeDtos));
        } catch (Exception e) {
            log.error("CostumeService::getCostumes execution failed with error {}", e.getMessage());
            throw exception(EntityType.COSTUME, ExceptionType.ENTITY_EXCEPTION, e.getMessage());
        }

        log.info("CostumeService::getCostumes execution completed");
        return costumeDtos;
    }

    public CostumeDto getCostume(Integer id) {
        log.info("CostumeService::getCostume execution started");
        CostumeDto costumeDto;

        log.debug("CostumeService::getCostume request parameters id {}", id);
        Costume costume = costumeRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("CostumeService::getCostume execution failed with invalid costume id {}", id);
                    throw exception(EntityType.COSTUME, ExceptionType.ENTITY_NOT_FOUND, id.toString());
                });

        costumeDto = CostumeMapper.toCostumeDto(costume);
        log.debug("CostumeService::getCostume received response from database {}", ValueMapper.jsonAsString(costumeDto));

        log.info("CostumeService::getCostume execution completed");
        return costumeDto;
    }

    public CostumeDto updateCostume(Integer id, CostumeRequest costumeRequest) {
        log.info("CostumeService::updateCostume execution started");
        CostumeDto costumeDto;

        log.debug("CostumeService::updateCostume request parameters id {}, costumeRequest {}", id, ValueMapper.jsonAsString(costumeRequest));
        Costume costume = costumeRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("CostumeService::updateCostume execution failed with invalid costume id {}", id);
                    throw exception(EntityType.COSTUME, ExceptionType.ENTITY_NOT_FOUND, id.toString());
                });

        costumeRepository.findByName(costumeRequest.getName())
                .ifPresent(duplicateCostume -> {
                    if (!duplicateCostume.getId().equals(costume.getId())) {
                        log.error("CostumeService::updateCostume execution failed with duplicate costume name {}", costumeRequest.getName());
                        throw exception(EntityType.COSTUME, ExceptionType.DUPLICATE_ENTITY, costumeRequest.getName());
                    }
                });

        Region region = null;
        boolean isSpecific = costumeRequest.getType().equals(CostumeType.SPECIFIC);
        if (isSpecific) {
            Integer regionId = costumeRequest.getRegionId();
            if(regionId == null) {
                log.error("CostumeService::updateCostume execution failed for specific type with null region id");
                throw exception(EntityType.REGION, ExceptionType.ENTITY_NOT_FOUND, "null");
            }

            region = regionRepository.findById(regionId)
                    .orElseThrow(() -> {
                        log.error("CostumeService::updateCostume execution failed for specific type with invalid region id {}", regionId);
                        throw exception(EntityType.REGION, ExceptionType.ENTITY_NOT_FOUND, regionId.toString());
                    });
        }

        try {
            Region applyRegion = isSpecific ? region : null;
            Costume updatedCostume = CostumeMapper.toCostume(costumeRequest, applyRegion);
            updatedCostume.setId(costume.getId());
            log.debug("CostumeService::updateCostume request parameters {}", ValueMapper.jsonAsString(updatedCostume));

            Costume savedCostume = costumeRepository.save(updatedCostume);
            costumeDto = CostumeMapper.toCostumeDto(savedCostume);
            log.debug("CostumeService::updateCostume received response from database {}", ValueMapper.jsonAsString(costumeDto));
        } catch (Exception e) {
            log.error("CostumeService::updateCostume execution failed with error {}", e.getMessage());
            throw exception(EntityType.COSTUME, ExceptionType.ENTITY_EXCEPTION, e.getMessage());
        }

        log.info("CostumeService::updateCostume execution completed");
        return costumeDto;
    }

    @Transactional
    public CostumeDto deleteCostume(Integer id) {
        log.info("CostumeService::deleteCostume execution started");
        CostumeDto costumeDto;

        log.debug("CostumeService::deleteCostume request parameters id {}", id);
        Costume costume = costumeRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("CostumeService::deleteCostume execution failed with invalid costume id {}", id);
                    throw exception(EntityType.COSTUME, ExceptionType.ENTITY_NOT_FOUND, id.toString());
                });

        try {
            costumeDto = CostumeMapper.toCostumeDto(costume);
            costumeRepository.delete(costume);
            log.debug("CostumeService::deleteCostume received response from database {}", ValueMapper.jsonAsString(costumeDto));
        } catch (Exception e) {
            log.error("CostumeService::deleteCostume execution failed with error {}", e.getMessage());
            throw exception(EntityType.COSTUME, ExceptionType.ENTITY_EXCEPTION, e.getMessage());
        }

        log.info("CostumeService::deleteCostume execution completed");
        return costumeDto;
    }

    private RuntimeException exception(EntityType entityType, ExceptionType exceptionType, String... args) {
        return ApplicationException.throwException(entityType, exceptionType, args);
    }
}
