package com.onix.worldtour.controller.api;

import com.onix.worldtour.controller.request.RegionRequest;
import com.onix.worldtour.dto.model.RegionDto;
import com.onix.worldtour.dto.response.Response;
import com.onix.worldtour.service.RegionService;
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
@RequestMapping("/api/v1/regions")
@Slf4j
public class RegionController {
    @Autowired
    private RegionService regionService;

    @PostMapping
    public ResponseEntity<Response> addRegion(@RequestBody @Valid RegionRequest regionRequest) {
        log.info("RegionController::addRegion request body {}", ValueMapper.jsonAsString(regionRequest));
        RegionDto regionDto = regionService.addRegion(regionRequest);

        Response<Object> response = Response.ok().setPayload(regionDto);
        log.info("RegionController::addRegion response {}", ValueMapper.jsonAsString(response));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Response> getRegions(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "search", defaultValue = "") String search,
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @RequestParam(value = "parentId", required = false) Integer parentId
    ) {
        log.info("RegionController::getRegions page {} size {} search {} categoryId {} parentId {}", page, size, search, categoryId, parentId);
        Page<RegionDto> regions = regionService.getRegions(page, size, search, categoryId, parentId);

        Response<Object> response = Response.ok().setPayload(regions);
        log.info("RegionController::getRegions response {}", ValueMapper.jsonAsString(response));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/options")
    public ResponseEntity<Response> getRegionOptions(
        @RequestParam(value = "search", defaultValue = "") String search,
        @RequestParam(value = "level", required = false) Integer level,
        @RequestParam(value = "lattitude", required = false) Double lattitude,
        @RequestParam(value = "longitude", required = false) Double longitude,
        @RequestParam(value = "radius", required = false) Double radius
    ) {
        log.info("RegionController::getRegionOptions search {} level {} lattitude {} longitude {} radius {}", search, level, lattitude, longitude, radius);
        List<RegionDto> regions = regionService.getRegionOptions(search, level, lattitude, longitude, radius);

        Response<Object> response = Response.ok().setPayload(regions);
        log.info("RegionController::getRegionOptions response {}", ValueMapper.jsonAsString(response));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getRegion(@PathVariable("id") Integer id) {
        log.info("RegionController::getRegion id {}", id);
        RegionDto regionDto = regionService.getRegion(id);

        Response<Object> response = Response.ok().setPayload(regionDto);
        log.info("RegionController::getRegion id {} response {}", id, ValueMapper.jsonAsString(response));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/import-countries")
    public ResponseEntity<Response> importCountries() {
        log.info("RegionController::importCountries");
        regionService.importCountries();

        Response<Object> response = Response.ok();
        log.info("RegionController::importCountries response {}", ValueMapper.jsonAsString(response));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response> updateRegion(@PathVariable("id") Integer id, @RequestBody @Valid RegionRequest regionRequest) {
        log.info("RegionController::updateRegion id {} request body {}", id, ValueMapper.jsonAsString(regionRequest));
        RegionDto region = regionService.updateRegion(id, regionRequest);

        Response<Object> response = Response.ok().setPayload(region);
        log.info("RegionController::updateRegion id {} response {}", id, ValueMapper.jsonAsString(response));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteRegion(@PathVariable("id") Integer id) {
        log.info("RegionController::deleteRegion id {}", id);
        RegionDto regionDto = regionService.deleteRegion(id);

        Response<Object> response = Response.ok().setPayload(regionDto);
        log.info("RegionController::deleteRegion id {} response {}", id, ValueMapper.jsonAsString(response));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
