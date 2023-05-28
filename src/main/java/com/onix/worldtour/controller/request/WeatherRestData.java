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
    private Current current;
    private Daily[] daily;


    @Data
    @Accessors(chain = true)
    public static class Current {
        private Double temp;
        private Long sunrise;
        private Long sunset;
        private Integer humidity;
        private Integer pressure;
        private Double wind_speed;
        private Integer wind_deg;
        private WeatherInfo[] weather;
    }

    @Data
    @Accessors(chain = true)
    public static class Daily {
        private Long sunrise;
        private Long sunset;
        private Temp temp;
        private WeatherInfo[] weather;
        private Integer humidity;
        private Integer pressure;
        private Double wind_speed;
        private Integer wind_deg;

        @Data
        @Accessors(chain = true)
        public static class Temp {
            private Double day;
            private Double night;
            private Double min;
            private Double max;
        }
    }

    @Data
    @Accessors(chain = true)
    public static class WeatherInfo {
        private String main;
        private String description;
        private String icon;
    }
}
