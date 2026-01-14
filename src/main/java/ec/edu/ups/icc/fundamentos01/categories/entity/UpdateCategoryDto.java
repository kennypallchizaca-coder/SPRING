package ec.edu.ups.icc.fundamentos01.categories.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateCategoryDto {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    public String name;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    public String description;
}
