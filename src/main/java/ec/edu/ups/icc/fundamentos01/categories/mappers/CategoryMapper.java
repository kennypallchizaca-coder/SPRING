package ec.edu.ups.icc.fundamentos01.categories.mappers;

import ec.edu.ups.icc.fundamentos01.categories.entity.CreateCategoryDto;
import ec.edu.ups.icc.fundamentos01.categories.entity.CategoryResponseDto;
import ec.edu.ups.icc.fundamentos01.categories.models.Category;

/**
 * Mapper para transformaciones de Category
 */
public class CategoryMapper {

    /**
     * Convierte CreateCategoryDto a modelo de dominio
     */
    public static Category fromCreateDto(CreateCategoryDto dto) {
        return Category.fromDto(dto);
    }

    /**
     * Convierte modelo de dominio a CategoryResponseDto
     */
    public static CategoryResponseDto toResponse(Category category) {
        CategoryResponseDto dto = new CategoryResponseDto();
        dto.id = category.getId();
        dto.name = category.getName();
        dto.description = category.getDescription();
        return dto;
    }
}
