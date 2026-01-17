package ec.edu.ups.icc.fundamentos01.categories.Services;

import ec.edu.ups.icc.fundamentos01.categories.entity.CreateCategoryDto;
import ec.edu.ups.icc.fundamentos01.categories.entity.UpdateCategoryDto;
import ec.edu.ups.icc.fundamentos01.categories.entity.CategoryEntity;
import ec.edu.ups.icc.fundamentos01.categories.entity.CategoryResponseDto;
import ec.edu.ups.icc.fundamentos01.categories.models.Category;
import ec.edu.ups.icc.fundamentos01.categories.repositories.CategoryRepository;
import ec.edu.ups.icc.fundamentos01.exception.domain.ConflictException;
import ec.edu.ups.icc.fundamentos01.exception.domain.NotFoundException;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.products.mappers.ProductMapper;
import ec.edu.ups.icc.fundamentos01.products.repositories.ProductRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación del servicio para operaciones de Category
 */
@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepo;
    private final ProductRepository productRepo;

    public CategoryServiceImpl(CategoryRepository categoryRepo, ProductRepository productRepo) {
        this.categoryRepo = categoryRepo;
        this.productRepo = productRepo;
    }

    @Override
    public List<CategoryResponseDto> findAll() {
        return categoryRepo.findAll()
                .stream()
                .map(Category::fromEntity)
                .map(this::toResponse)
                .toList();
    }

    @Override
    public CategoryResponseDto findOne(Long id) {
        return categoryRepo.findById(id)
                .map(Category::fromEntity)
                .map(this::toResponse)
                .orElseThrow(() -> new NotFoundException("Categoría no encontrada con ID: " + id));
    }

    @Override
    public CategoryResponseDto create(CreateCategoryDto dto) {

        // Validar que el nombre sea único
        if (categoryRepo.existsByName(dto.name)) {
            throw new ConflictException("Ya existe una categoría con el nombre: " + dto.name);
        }

        // Crear modelo de dominio
        Category category = Category.fromDto(dto);

        // Convertir a entidad y persistir
        CategoryEntity entity = category.toEntity();
        CategoryEntity saved = categoryRepo.save(entity);

        // Retornar DTO de respuesta
        return toResponse(Category.fromEntity(saved));
    }

    @Override
    public CategoryResponseDto update(Long id, UpdateCategoryDto dto) {

        // Buscar categoría existente
        CategoryEntity existing = categoryRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Categoría no encontrada con ID: " + id));

        // Validar que el nuevo nombre sea único (si cambió)
        if (!existing.getName().equals(dto.name) && categoryRepo.existsByName(dto.name)) {
            throw new ConflictException("Ya existe otra categoría con el nombre: " + dto.name);
        }

        // Actualizar usando dominio
        Category category = Category.fromEntity(existing);
        category.update(dto);

        // Persistir cambios
        CategoryEntity updated = category.toEntity();
        updated.setId(id); // Mantener el ID
        CategoryEntity saved = categoryRepo.save(updated);

        return toResponse(Category.fromEntity(saved));
    }

    @Override
    public void delete(Long id) {

        CategoryEntity category = categoryRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Categoría no encontrada con ID: " + id));

        // Eliminación física
        categoryRepo.delete(category);
    }

    @Override
    public Long countProductsByCategoryId(Long categoryId) {
        // Verificar que la categoría existe
        CategoryEntity category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Categoría no encontrada con ID: " + categoryId));

        // Retornar el conteo de productos asociados
        return (long) category.getProducts().size();
    }

    @Override
    public List<ProductResponseDto> getProductsByCategoryId(Long categoryId) {
        categoryRepo.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Categoria no encontrada con ID: " + categoryId));

        return productRepo.findByCategoriesId(categoryId)
                .stream()
                .map(ProductMapper::toResponse)
                .toList();
    }

    private CategoryResponseDto toResponse(Category category) {
        CategoryResponseDto dto = new CategoryResponseDto();
        dto.id = category.getId();
        dto.name = category.getName();
        dto.description = category.getDescription();
        return dto;
    }

}
