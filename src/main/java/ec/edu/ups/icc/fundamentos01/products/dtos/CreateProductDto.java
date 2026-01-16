package ec.edu.ups.icc.fundamentos01.products.dtos;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateProductDto {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 200, message = "El nombre debe tener entre 3 y 200 caracteres")
    public String name;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    public String description;

    @NotNull(message = "El precio es obligatorio")
    @Min(value = 0, message = "El precio no puede ser negativo")
    public double price;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    public int stock;

    @NotNull(message = "El ID del usuario es obligatorio")
    public long userId;
    @NotNull(message = "El ID de la categoría es obligatorio")
    public long categoryId;

    public List<Long> categoryIds;//[4,2]
}
