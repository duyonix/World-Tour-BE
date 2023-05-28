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
        String iconUrl = "http://openweathermap.org/img/wn";

        WeatherDto weatherDto = new WeatherDto();
        WeatherRestData.Current current = data.getCurrent();
        WeatherRestData.Daily[] daily = data.getDaily();

        weatherDto.setCurrent(new WeatherDto.Current()
                .setMain(mapper.get(current.getWeather()[0].getMain()))
                .setDescription(current.getWeather()[0].getDescription())
                .setIcon(String.format("%s/%s@4x.png", iconUrl, current.getWeather()[0].getIcon()))
                .setTemp(current.getTemp().toString() + "°C")
                .setHumidity(current.getHumidity().toString() + "%")
                .setPressure(current.getPressure().toString() + "mb")
                .setWind(new WeatherDto.Wind()
                        .setSpeed(current.getWind_speed().toString() + "m/s")
                        .setDeg(current.getWind_deg().toString() + "°"))
                .setSunrise(convertTimestampToDateTime(current.getSunrise()))
                .setSunset(convertTimestampToDateTime(current.getSunset())));

        // match daily weather
        WeatherDto.Daily[] dailyDto = new WeatherDto.Daily[daily.length];
        for (int i = 0; i < daily.length; i++) {
            dailyDto[i] = new WeatherDto.Daily()
                    .setMain(mapper.get(daily[i].getWeather()[0].getMain()))
                    .setDescription(daily[i].getWeather()[0].getDescription())
                    .setIcon(String.format("%s/%s@4x.png", iconUrl, daily[i].getWeather()[0].getIcon()))
                    .setTemp(new WeatherDto.Temp()
                            .setDay(daily[i].getTemp().getDay().toString() + "°C")
                            .setNight(daily[i].getTemp().getNight().toString() + "°C")
                            .setMax(daily[i].getTemp().getMax().toString() + "°C")
                            .setMin(daily[i].getTemp().getMin().toString() + "°C"))
                    .setHumidity(daily[i].getHumidity().toString() + "%")
                    .setPressure(daily[i].getPressure().toString() + "mb")
                    .setWind(new WeatherDto.Wind()
                            .setSpeed(daily[i].getWind_speed().toString() + "m/s")
                            .setDeg(daily[i].getWind_deg().toString() + "°"))
                    .setSunrise(convertTimestampToDateTime(daily[i].getSunrise()))
                    .setSunset(convertTimestampToDateTime(daily[i].getSunset()));
        }
        weatherDto.setDaily(dailyDto);
        return weatherDto;
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
