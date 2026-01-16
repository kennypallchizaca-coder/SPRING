package ec.edu.ups.icc.fundamentos01.products.mappers;

import ec.edu.ups.icc.fundamentos01.categories.entity.CategoryResponseDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.CreateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.products.entities.ProductEntity;
import ec.edu.ups.icc.fundamentos01.products.models.Product;

public class ProductMapper {

    /**
     * Crea un Product desde CreateProductDto
     */
    public static Product fromCreateDto(CreateProductDto dto) {
        return Product.fromDto(dto);
    }

    /**
     * Convierte Product a ProductResponseDto (sin relaciones)
     */
    public static ProductResponseDto toResponse(Product product) {
        ProductResponseDto dto = new ProductResponseDto();
        dto.id = product.getId();
        dto.name = product.getName();
        dto.description = product.getDescription();
        dto.price = product.getPrice();
        dto.stock = product.getStock();
        return dto;
    }

    /**
     * Convierte ProductEntity a ProductResponseDto (con relaciones completas)
     */
    public static ProductResponseDto toResponse(ProductEntity entity) {
        ProductResponseDto dto = new ProductResponseDto();

        // Campos básicos
        dto.id = entity.getId();
        dto.name = entity.getName();
        dto.description = entity.getDescription();
        dto.price = entity.getPrice();
        dto.stock = entity.getStock();

        // Crear objeto User anidado
        ProductResponseDto.UserSummaryDto userDto = new ProductResponseDto.UserSummaryDto();
        userDto.id = entity.getOwner().getId();
        userDto.name = entity.getOwner().getName();
        userDto.email = entity.getOwner().getEmail();
        dto.user = userDto;

        // Crear objeto Category anidado
        CategoryResponseDto categoryDto = new CategoryResponseDto();
        categoryDto.id = entity.getCategory().getId();
        categoryDto.name = entity.getCategory().getName();
        categoryDto.description = entity.getCategory().getDescription();
        dto.category = categoryDto;

        // Auditoría
        dto.createdAt = entity.getCreatedAt();
        dto.updatedAt = entity.getUpdatedAt();

        return dto;
    }
}
