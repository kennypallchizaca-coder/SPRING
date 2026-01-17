package ec.edu.ups.icc.fundamentos01.categories.Services;

import java.util.List;

import ec.edu.ups.icc.fundamentos01.categories.entity.CategoryResponseDto;
import ec.edu.ups.icc.fundamentos01.categories.entity.CreateCategoryDto;
import ec.edu.ups.icc.fundamentos01.categories.entity.UpdateCategoryDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;

public interface CategoryService {

    List<CategoryResponseDto> findAll();

    CategoryResponseDto findOne(Long id);

    CategoryResponseDto create(CreateCategoryDto dto);

    CategoryResponseDto update(Long id, UpdateCategoryDto dto);

    void delete(Long id);

    // Cuenta productos por categoria.
    Long countProductsByCategoryId(Long categoryId);

    List<ProductResponseDto> getProductsByCategoryId(Long categoryId);
}
