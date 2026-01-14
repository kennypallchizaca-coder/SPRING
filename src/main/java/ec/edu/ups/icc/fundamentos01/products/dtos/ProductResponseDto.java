package ec.edu.ups.icc.fundamentos01.products.dtos;

import ec.edu.ups.icc.fundamentos01.categories.entity.CategoryResponseDto;
import java.time.LocalDateTime;

public class ProductResponseDto {
    public Long id;
    public String name;
    public String description;
    public double price;
    public Integer stock;

    // Relaciones con estructura anidada
    public UserSummaryDto user; // Corregido de userId
    public CategoryResponseDto category; // Corregido de categoryId

    // Auditoría
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;

    public static class UserSummaryDto {
        public Long id;
        public String name;
        public String email;
    }

}
