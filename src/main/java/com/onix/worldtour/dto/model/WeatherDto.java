package com.onix.worldtour.dto.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
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
    private Current current;
    private Daily[] daily;

    @Data
    @Accessors(chain = true)
    public static class Temp {
        private String day;
        private String night;
        private String min;
        private String max;
    }

    @Data
    @Accessors(chain = true)
    public static class Wind {
        private String speed;
        private String deg;
    }

    @Data
    @Accessors(chain = true)
    public static class Current {
        private String main;
        private String description;
        private String background;
        private String icon;
        private String temp;
        private String humidity;
        private String pressure;
        private Wind wind;
        private Long sunrise;
        private Long sunset;
    }

    @Data
    @Accessors(chain = true)
    public static class Daily {
        private String main;
        private String description;
        private String background;
        private String icon;
        private Temp temp;
        private String humidity;
        private String pressure;
        private Wind wind;
        private Long sunrise;
        private Long sunset;
    }
}
