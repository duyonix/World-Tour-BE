package com.onix.worldtour.dto.mapper;

import com.onix.worldtour.controller.request.WeatherRestData;
import com.onix.worldtour.dto.model.WeatherDto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

public class WeatherMapper {
    public static WeatherDto toWeatherDto(WeatherRestData data) {
        Map<String, String> mapper = mainWeatherMapper();
        return new WeatherDto()
                .setMain(mapper.get(data.getWeather()[0].getMain()))
                .setDescription(data.getWeather()[0].getDescription())
                .setIcon(String.format("http://openweathermap.org/img/wn/%s@4x.png", data.getWeather()[0].getIcon()))
                .setTemperature(new WeatherDto.Temperature()
                        .setAverage(data.getMain().getTemp().toString() + "°C")
                        .setHigh(data.getMain().getTemp_max().toString() + "°C")
                        .setLow(data.getMain().getTemp_min().toString() + "°C"))
                .setHumidity(data.getMain().getHumidity().toString() + "%")
                .setPressure(data.getMain().getPressure().toString() + "mb")
                .setWind(new WeatherDto.Wind()
                        .setSpeed(data.getWind().getSpeed().toString() + "m/s")
                        .setDeg(data.getWind().getDeg().toString() + "°"))
                .setSunrise(convertTimestampToDateTime(data.getSys().getSunrise()))
                .setSunset(convertTimestampToDateTime(data.getSys().getSunset()));
    }

    private static Map<String, String> mainWeatherMapper() {
       Map<String, String> mapper = new HashMap<>();
        mapper.put("Clear", "Trời quang");
        mapper.put("Clouds", "Mây");
        mapper.put("Rain", "Mưa");
        mapper.put("Drizzle", "Mưa phùn");
        mapper.put("Thunderstorm", "Dông");
        mapper.put("Snow", "Tuyết");
        mapper.put("Mist", "Sương mù");
        mapper.put("Smoke", "Khói");
        mapper.put("Haze", "Sương mù");
        mapper.put("Dust", "Bụi");
        mapper.put("Fog", "Sương mù");
        mapper.put("Sand", "Cát");
        mapper.put("Ash", "Tro");
        mapper.put("Squall", "Gió mạnh");
        mapper.put("Tornado", "Lốc xoáy");

       return mapper;
    }

    private static LocalDateTime convertTimestampToDateTime(Long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault());
    }
}
