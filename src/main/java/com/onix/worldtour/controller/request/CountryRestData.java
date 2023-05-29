package com.onix.worldtour.controller.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Map;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CountryRestData {
    private NameCountry name;
    private String cca2;
    private String[] tld;
    private String[] latlng;
    private FlagCountry flags;
    private Long population;
    private Double area;
    private Map<String, Object> currencies;
    private String[] capital;
    private String subregion;
    private Map<String, String> languages;
    private String[] timezones;


    @Data
    @Accessors(chain = true)
    public static class NameCountry {
        private String official;
        private String common;
    }

    @Data
    @Accessors(chain = true)
    public static class FlagCountry {
        private String png;
        private String svg;
    }
}
