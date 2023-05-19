package com.onix.worldtour.controller.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherRestData {
    private Weather[] weather;
    private Main main;
    private Wind wind;
    private Sys sys;

    @Data
    @Accessors(chain = true)
    public static class Weather {
        private String main;
        private String description;
        private String icon;
    }

    @Data
    @Accessors(chain = true)
    public static class Main {
        private Double temp;
        private Double temp_min;
        private Double temp_max;
        private Double pressure;
        private Double humidity;
    }

    @Data
    @Accessors(chain = true)
    public static class Wind {
        private Double speed;
        private Double deg;
    }

    @Data
    @Accessors(chain = true)
    public static class Sys {
        private Long sunrise;
        private Long sunset;
    }
}
