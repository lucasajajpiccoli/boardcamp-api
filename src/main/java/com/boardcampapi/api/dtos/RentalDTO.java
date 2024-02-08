package com.boardcampapi.api.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class RentalDTO {
    
    @NotNull(message = "Field customerId cannot be null")
    private Long customerId;

    @NotNull(message = "Field gameId cannot be null")
    private Long gameId;

    @NotNull(message = "Field daysRented cannot be null")
    @Positive(message = "Field daysRented must be positive")
    private Long daysRented;
}
