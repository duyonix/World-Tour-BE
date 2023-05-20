package com.onix.worldtour.controller.api;

import com.onix.worldtour.controller.request.CostumeRequest;
import com.onix.worldtour.dto.model.CostumeDto;
import com.onix.worldtour.dto.response.Response;
import com.onix.worldtour.service.CostumeService;
import com.onix.worldtour.util.ValueMapper;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/costumes")
@Slf4j
public class CostumeController {
    @Autowired
    private CostumeService costumeService;

    @PostMapping
    public ResponseEntity<Response> addCostume(@RequestBody @Valid CostumeRequest costumeRequest) {
        log.info("CostumeController::addCostume request body {}", ValueMapper.jsonAsString(costumeRequest));
        CostumeDto costumeDto = costumeService.addCostume(costumeRequest);

        Response<Object> response = Response.ok().setPayload(costumeDto);
        log.info("CostumeController::addCostume response {}", ValueMapper.jsonAsString(response));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Response> getCostumes(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "search", defaultValue = "") String search,
            @RequestParam(value = "regionId", required = false) Integer regionId,
            @RequestParam(value = "type", required = false) String type
    ) {
        log.info("CostumeController::getCostumes page {} size {} search {} regionId {} type {}", page, size, search, regionId, type);
        Page<CostumeDto> costumes = costumeService.getCostumes(page, size, search, regionId, type);

        Response<Object> response = Response.ok().setPayload(costumes);
        log.info("CostumeController::getCostumes response {}", ValueMapper.jsonAsString(response));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/for-region-and-ancestors/{regionId}")
    public ResponseEntity<Response> getCostumesForRegionAndAncestors(@PathVariable("regionId") Integer regionId) {
        log.info("CostumeController::getCostumesForRegionAndAncestors by regionId {}", regionId);
        List<CostumeDto> costumes = costumeService.getCostumesForRegionAndAncestors(regionId);

        Response<Object> response = Response.ok().setPayload(costumes);
        log.info("CostumeController::getCostumesForRegionAndAncestors by regionId {} response {}", regionId, ValueMapper.jsonAsString(response));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getCostume(@PathVariable("id") Integer id) {
        log.info("CostumeController::getCostume by id {}", id);
        CostumeDto costume = costumeService.getCostume(id);

        Response<Object> response = Response.ok().setPayload(costume);
        log.info("CostumeController::getCostume by id {} response {}", id, ValueMapper.jsonAsString(response));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response> updateCostume(@PathVariable("id") Integer id, @RequestBody @Valid CostumeRequest costumeRequest) {
        log.info("CostumeController::updateCostume id {} request body {}", id, ValueMapper.jsonAsString(costumeRequest));
        CostumeDto costumeDto = costumeService.updateCostume(id, costumeRequest);

        Response<Object> response = Response.ok().setPayload(costumeDto);
        log.info("CostumeController::updateCostume id {} response {}", id, ValueMapper.jsonAsString(response));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteCostume(@PathVariable("id") Integer id) {
        log.info("CostumeController::deleteCostume id {}", id);
        CostumeDto costumeDto = costumeService.deleteCostume(id);

        Response<Object> response = Response.ok().setPayload(costumeDto);
        log.info("CostumeController::deleteCostume id {} response {}", id, ValueMapper.jsonAsString(response));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}