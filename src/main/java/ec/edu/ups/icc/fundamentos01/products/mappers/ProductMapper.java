package ec.edu.ups.icc.fundamentos01.products.mappers;

import ec.edu.ups.icc.fundamentos01.categories.entity.CategoryEntity;
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

        dto.id = entity.getId();
        dto.name = entity.getName();
        dto.description = entity.getDescription();
        dto.price = entity.getPrice();
        dto.stock = entity.getStock();

        ProductResponseDto.UserSummaryDto userDto = new ProductResponseDto.UserSummaryDto();
        userDto.id = entity.getOwner().getId();
        userDto.name = entity.getOwner().getName();
        userDto.email = entity.getOwner().getEmail();
        dto.user = userDto;

        dto.categories = entity.getCategories().stream()
                .map(ProductMapper::toCategoryResponseDto)
                .sorted((left, right) -> left.name.compareToIgnoreCase(right.name))
                .toList();

        dto.createdAt = entity.getCreatedAt();
        dto.updatedAt = entity.getUpdatedAt();

        return dto;
    }

    private static CategoryResponseDto toCategoryResponseDto(CategoryEntity category) {
        CategoryResponseDto dto = new CategoryResponseDto();
        dto.id = category.getId();
        dto.name = category.getName();
        dto.description = category.getDescription();
        return dto;
    }
}
