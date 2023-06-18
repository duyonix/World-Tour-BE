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
public class SceneSpotRequest {
    private Integer id;

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotBlank(message = "Picture is required")
    private String picture;

    private String panorama;

    private String review;
}
