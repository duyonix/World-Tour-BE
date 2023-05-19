package com.onix.worldtour.service;

import com.onix.worldtour.controller.request.WeatherRestData;
import com.onix.worldtour.dto.mapper.WeatherMapper;
import com.onix.worldtour.dto.model.WeatherDto;
import com.onix.worldtour.model.Coordinate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {
    private static final String API_KEY = "5e81a03cdaa47006f6c62c062056d455";
    private static final String API_URL = "https://api.openweathermap.org/data/2.5/weather";

    private final RestTemplate restTemplate;

    public WeatherService() {
        this.restTemplate = new RestTemplate();
    }

    public WeatherDto getWeather(Coordinate coordinate) {
        String apiUrl = buildApiUrl(coordinate);

        WeatherRestData weatherRestData = restTemplate.getForObject(apiUrl, WeatherRestData.class);
        WeatherDto weatherDto = WeatherMapper.toWeatherDto(weatherRestData);

        return weatherDto;
    }

    private String buildApiUrl(Coordinate coordinate) {
        String lat = String.valueOf(coordinate.getLattitude());
        String lon = String.valueOf(coordinate.getLongitude());

        String apiUrl = String.format("%s?lat=%s&lon=%s&appid=%s&units=metric&lang=vi", API_URL, lat, lon, API_KEY);
        return apiUrl;
    }
}
