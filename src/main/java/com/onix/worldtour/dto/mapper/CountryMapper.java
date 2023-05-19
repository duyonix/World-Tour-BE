package com.onix.worldtour.dto.mapper;

import com.onix.worldtour.controller.request.CountryRequest;
import com.onix.worldtour.controller.request.CountryRestData;
import com.onix.worldtour.dto.model.CountryDto;
import com.onix.worldtour.model.Country;

import java.util.Map;

public class CountryMapper {
    public static CountryRequest toCountryRequest(CountryRestData data) {
        return new CountryRequest()
                .setCode(data.getCca2())
                .setCapital(data.getCapital() != null ? data.getCapital()[0] : null)
                .setCurrency(mapCurrency(data.getCurrencies()))
                .setLanguage(mapLanguage(data.getLanguages()))
                .setTimezone(data.getTimezones() != null ? data.getTimezones()[0] : null);
    }

    public static Country toCountry(CountryRequest countryRequest) {
        return new Country()
                .setId(countryRequest.getId())
                .setCode(countryRequest.getCode())
                .setCapital(countryRequest.getCapital())
                .setLanguage(countryRequest.getLanguage())
                .setCurrency(countryRequest.getCurrency())
                .setTimezone(countryRequest.getTimezone());
    }

    public static CountryDto toCountryDto(Country country) {
        return new CountryDto()
                .setId(country.getId())
                .setCode(country.getCode())
                .setCapital(country.getCapital())
                .setLanguage(country.getLanguage())
                .setCurrency(country.getCurrency())
                .setTimezone(country.getTimezone());
    }

    private static String mapCurrency(Map<String, Object> currencies) {
        if (currencies == null || currencies.isEmpty()) {
            return null;
        }

        String currencyCode = currencies.keySet().stream().findFirst().orElse(null);
        Object currencyValue = currencies.values().stream().findFirst().orElse(null);

        if (currencyCode != null && currencyValue != null) {
            if (currencyValue instanceof Map) {
                Map<String, Object> currencyValueMap = (Map<String, Object>) currencyValue;
                Object currencyName = currencyValueMap.get("name");
                if (currencyName != null) {
                    return currencyName.toString() + " (" + currencyCode + ")";
                }
            }
        }

        return null;
    }

    private static String mapLanguage(Map<String, String> languages) {
        if (languages == null || languages.isEmpty()) {
            return null;
        }

        return languages.values().stream().findFirst().orElse(null);
    }
}
