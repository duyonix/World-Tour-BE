package com.onix.worldtour.controller.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CountryRequest {
    @NotBlank(message = "Code is required")
    private String code;

    @NotBlank(message = "Capital is required")
    private String capital;

    @NotBlank(message = "Language is required")
    private String language;

    @NotBlank(message = "Currency is required")
    private String currency;

    @NotBlank(message = "Timezone is required")
    private String timezone;
}
