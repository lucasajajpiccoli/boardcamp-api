package com.boardcampapi.api.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GameDTO {
    
    @NotBlank(message = "Field name cannot be null, empty or blank")
    private String name;

    @NotBlank(message = "Field image cannot be null, empty or blank")
    @Pattern(
        regexp="^(https?:\\/\\/).*$",
        message = "Field image must be http:// or https:// URL"
    )
    private String image;

    @NotNull(message = "Field stockTotal cannot be null")
    @Positive(message = "Field stockTotal must be positive")
    private Long stockTotal;

    @NotNull(message = "Field pricePerDay cannot be null")
    @Positive(message = "Field pricePerDay must be positive")
    private Long pricePerDay;

}
