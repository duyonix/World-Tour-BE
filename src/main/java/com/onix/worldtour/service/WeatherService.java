package com.onix.worldtour.service;

import com.onix.worldtour.controller.data.WeatherRestData;
import com.onix.worldtour.dto.mapper.WeatherMapper;
import com.onix.worldtour.dto.model.WeatherDto;
import com.onix.worldtour.exception.ApplicationException;
import com.onix.worldtour.exception.EntityType;
import com.onix.worldtour.exception.ExceptionType;
import com.onix.worldtour.model.Coordinate;
import com.onix.worldtour.model.Weather;
import com.onix.worldtour.repository.WeatherRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@Slf4j
public class WeatherService {

    @Autowired
    WeatherRepository weatherRepository;

    private static final String API_KEY = "5e81a03cdaa47006f6c62c062056d455";
    private static final String API_URL = "https://api.openweathermap.org/data/2.5/onecall";

    private final RestTemplate restTemplate;

    public WeatherService() {
        this.restTemplate = new RestTemplate();
    }

    public WeatherDto getWeather(Coordinate coordinate) {
        log.info("WeatherService::getWeather execution started");
        WeatherDto weatherDto = null;

        try {
            log.debug("WeatherService::getWeather coordinate {}", coordinate);
            String apiUrl = buildApiUrl(coordinate);

            List<Weather> weathers = weatherRepository.findAll();

            WeatherRestData weatherRestData = restTemplate.getForObject(apiUrl, WeatherRestData.class);
            if (weatherRestData != null) {
                weatherDto = WeatherMapper.toWeatherDto(weatherRestData, weathers);
            }
            log.debug("WeatherService::getWeather received response {}", weatherDto);
        } catch (Exception e) {
            log.error("WeatherService::getWeather error {}", e.getMessage());
            throw exception(EntityType.WEATHER, ExceptionType.ENTITY_EXCEPTION, coordinate.toString());
        }

        log.info("WeatherService::getWeather execution completed");
        return weatherDto;
    }

    private String buildApiUrl(Coordinate coordinate) {
        String lat = String.valueOf(coordinate.getLattitude());
        String lon = String.valueOf(coordinate.getLongitude());

        String apiUrl = String.format("%s?lat=%s&lon=%s&appid=%s&exclude=hourly,minutely&units=metric&lang=vi", API_URL, lat, lon, API_KEY);
        return apiUrl;
    }

    private RuntimeException exception(EntityType entityType, ExceptionType exceptionType, String... args) {
        return ApplicationException.throwException(entityType, exceptionType, args);
    }
}
