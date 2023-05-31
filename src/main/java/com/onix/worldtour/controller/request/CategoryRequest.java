package com.onix.worldtour.controller.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CategoryRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Level is required")
    private Integer level;

    @NotNull(message = "Zoom factor is required")
    private Double zoomFactor;

    @NotNull(message = "Picture is required")
    private String picture;

    private String description;
}
