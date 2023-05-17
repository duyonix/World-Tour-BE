package com.onix.worldtour.dto.mapper;

import com.onix.worldtour.controller.request.CountryRequest;
import com.onix.worldtour.dto.model.CountryDto;
import com.onix.worldtour.model.Country;

public class CountryMapper {
    public static Country toCountry(CountryRequest countryRequest) {
        return new Country()
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
}
