package com.onix.worldtour.dto.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WeatherDto {
    private String main;
    private String description;
    private String icon;
    private Temperature temperature;
    private String humidity;
    private String pressure;
    private Wind wind;
    private LocalDateTime sunrise;
    private LocalDateTime sunset;

    @Data
    @Accessors(chain = true)
    public static class Temperature {
        private String average;
        private String high;
        private String low;
    }

    @Data
    @Accessors(chain = true)
    public static class Wind {
        private String speed;
        private String deg;
    }
}
