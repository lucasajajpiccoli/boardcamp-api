package com.boardcampapi.api.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CustomerDTO {

    @NotBlank(message = "Field name cannot be null, empty or blank")
    private String name;

    @NotBlank(message = "Field cpf cannot be null, empty or blank")
    @Size(min = 11, max = 11, message = "Field cpf must have exactly 11 digits")
    private String cpf;
    
}
