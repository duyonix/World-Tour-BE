package com.onix.worldtour.controller.data;

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
public class StateData {
    private Integer id;
    private String name;
    private Integer country_id;
    private String country_code;
    private String country_name;
    private String state_code;
    private String type;
    private String latitude;
    private String longitude;
}
