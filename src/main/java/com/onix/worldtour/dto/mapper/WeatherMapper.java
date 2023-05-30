package com.onix.worldtour.dto.mapper;

import com.onix.worldtour.controller.data.WeatherRestData;
import com.onix.worldtour.dto.model.WeatherDto;
import com.onix.worldtour.model.Weather;
import com.onix.worldtour.util.Util;

import java.util.List;

public class WeatherMapper {
    public static WeatherDto toWeatherDto(WeatherRestData data, List<Weather> weather) {
        String iconUrl = "http://openweathermap.org/img/wn";

        WeatherDto weatherDto = new WeatherDto();
        WeatherRestData.Current current = data.getCurrent();
        WeatherRestData.Daily[] daily = data.getDaily();

        Weather currentWeather = weather.stream()
                .filter(w -> w.getName().equals(current.getWeather()[0].getMain()))
                .findFirst()
                .orElse(weather.get(0));

        weatherDto.setCurrent(new WeatherDto.Current()
                .setMain(currentWeather.getVietnameseName())
                .setBackground(currentWeather.getBackground())
                .setDescription(current.getWeather()[0].getDescription())
                .setIcon(String.format("%s/%s@4x.png", iconUrl, current.getWeather()[0].getIcon()))
                .setTemp(current.getTemp().toString() + "°C")
                .setHumidity(current.getHumidity().toString() + "%")
                .setPressure(current.getPressure().toString() + "mb")
                .setWind(new WeatherDto.Wind()
                        .setSpeed(current.getWind_speed().toString() + "m/s")
                        .setDeg(current.getWind_deg().toString() + "°"))
                .setSunrise(current.getSunrise())
                .setSunset(current.getSunset()));

        // match daily weather
        WeatherDto.Daily[] dailyDto = new WeatherDto.Daily[daily.length];
        for (int i = 0; i < daily.length; i++) {
            int finalI = i;
            Weather dailyWeather = weather.stream()
                    .filter(w -> w.getName().equals(daily[finalI].getWeather()[0].getMain()))
                    .findFirst()
                    .orElse(weather.get(0));

            dailyDto[i] = new WeatherDto.Daily()
                    .setMain(dailyWeather.getVietnameseName())
                    .setBackground(dailyWeather.getBackground())
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
                    .setSunrise(daily[i].getSunrise())
                    .setSunset(daily[i].getSunset());
        }
        weatherDto.setDaily(dailyDto);
        return weatherDto;
    }
}
