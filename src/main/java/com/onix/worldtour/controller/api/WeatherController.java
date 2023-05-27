package com.onix.worldtour.controller.api;

import com.onix.worldtour.dto.model.WeatherDto;
import com.onix.worldtour.dto.response.Response;
import com.onix.worldtour.model.Coordinate;
import com.onix.worldtour.service.WeatherService;
import com.onix.worldtour.util.ValueMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/weather")
@Slf4j
public class WeatherController {
    @Autowired
    private WeatherService weatherService;

    @GetMapping
    public ResponseEntity<Response> getWeather(
            @RequestParam(value = "latitude", required = true) Double latitude,
            @RequestParam(value = "longitude", required = true) Double longitude
    ) {
        log.info("WeatherController::getWeather latitude {} longitude {}", latitude, longitude);
        Coordinate coordinate = new Coordinate(latitude, longitude);
        WeatherDto weatherDto = weatherService.getWeather(coordinate);

        Response<Object> response = Response.ok().setPayload(weatherDto);
        log.info("WeatherController::getWeather response {}", ValueMapper.jsonAsString(response));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
