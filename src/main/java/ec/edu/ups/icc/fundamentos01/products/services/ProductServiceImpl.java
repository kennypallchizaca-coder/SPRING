package ec.edu.ups.icc.fundamentos01.products.services;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

import org.springframework.stereotype.Service;

import ec.edu.ups.icc.fundamentos01.categories.entity.CategoryEntity;
import ec.edu.ups.icc.fundamentos01.categories.entity.CategoryResponseDto;
import ec.edu.ups.icc.fundamentos01.categories.repositories.CategoryRepository;
import ec.edu.ups.icc.fundamentos01.products.dtos.CreateProductDto;
import ec.edu.ups.icc.fundamentos01.exception.domain.NotFoundException;
import ec.edu.ups.icc.fundamentos01.exception.domain.ConflictException;
import ec.edu.ups.icc.fundamentos01.products.dtos.PartialUpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.UpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.entities.ProductEntity;
import ec.edu.ups.icc.fundamentos01.products.models.Product;
import ec.edu.ups.icc.fundamentos01.products.repositories.ProductRepository;
import ec.edu.ups.icc.fundamentos01.users.entities.UserEntity;
import ec.edu.ups.icc.fundamentos01.users.repositories.UserRepository;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepo;
    private final UserRepository userRepo;
    private final CategoryRepository categoryRepo;

    public ProductServiceImpl(ProductRepository productRepo, UserRepository userRepo, CategoryRepository categoryRepo) {
        this.productRepo = productRepo;
        this.userRepo = userRepo;
        this.categoryRepo = categoryRepo;
    }

    private Set<CategoryEntity> validateAndGetCategories(Set<Long> categoryIds) {
        Set<CategoryEntity> categories = new HashSet<>();
        for (Long categoryId : categoryIds) {
            CategoryEntity category = categoryRepo.findById(categoryId)
                    .orElseThrow(() -> new NotFoundException("Categoría no encontrada con ID: " + categoryId));
            categories.add(category);
        }
        return categories;
    }

    private CategoryEntity validateCategory(Long categoryId) {
        return categoryRepo.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Categoría no encontrada con ID: " + categoryId));
    }

    @Override
    public List<ProductResponseDto> findAll() {
        return productRepo.findAll()
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Override
    public ProductResponseDto findOne(int id) {
        ProductEntity entity = productRepo.findById((long) id)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado"));
        return toResponseDto(entity);
    }
    @Override
    public ProductResponseDto create(CreateProductDto dto) {
        if (productRepo.findByName(dto.name).isPresent()) {
            throw new ConflictException("Ya existe un producto con el nombre: " + dto.name);
        }

        UserEntity owner = userRepo.findById(dto.userId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado con ID: " + dto.userId));

        Set<CategoryEntity> categories = validateAndGetCategories(dto.categoryIds);

        Product newProduct = Product.fromDto(dto);
        ProductEntity entity = newProduct.toEntity(owner);
        entity.setCategories(categories);

        ProductEntity saved = productRepo.save(entity);

        return toResponseDto(saved);

    }

    private ProductResponseDto toResponseDto(ProductEntity entity) {
        ProductResponseDto dto = new ProductResponseDto();

        // Campos básicos
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
                .map(this::toCategoryResponseDto)
                .sorted((left, right) -> left.name.compareToIgnoreCase(right.name))
                .toList();

        dto.createdAt = entity.getCreatedAt();
        dto.updatedAt = entity.getUpdatedAt();

        return dto;
    }

    private CategoryResponseDto toCategoryResponseDto(CategoryEntity category) {
        CategoryResponseDto dto = new CategoryResponseDto();
        dto.id = category.getId();
        dto.name = category.getName();
        dto.description = category.getDescription();
        return dto;
    }

    @Override
    public ProductResponseDto update(int id, UpdateProductDto dto) {
        productRepo.findByName(dto.name).ifPresent(existing -> {
            if (existing.getId() != id) {
                throw new ConflictException("Ya existe otro producto con el nombre: " + dto.name);
            }
        });

        ProductEntity existingEntity = productRepo.findById((long) id)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado"));

        existingEntity.setName(dto.name);
        existingEntity.setDescription(dto.description);
        existingEntity.setPrice(dto.price);
        existingEntity.setStock(dto.stock);

        Set<CategoryEntity> categories = validateAndGetCategories(dto.categoryIds);
        existingEntity.clearCategories();
        existingEntity.setCategories(categories);

        ProductEntity saved = productRepo.save(existingEntity);
        return toResponseDto(saved);
    }

    @Override
    public ProductResponseDto partialUpdate(int id, PartialUpdateProductDto dto) {
        ProductEntity existingEntity = productRepo.findById((long) id)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado"));

        if (dto.name != null) {
            existingEntity.setName(dto.name);
        }
        if (dto.description != null) {
            existingEntity.setDescription(dto.description);
        }
        if (dto.price != null) {
            existingEntity.setPrice(dto.price);
        }
        if (dto.stock != null) {
            existingEntity.setStock(dto.stock);
        }

        ProductEntity saved = productRepo.save(existingEntity);
        return toResponseDto(saved);
    }

    @Override
    public void delete(int id) {
        productRepo.findById((long) id)
                .ifPresentOrElse(
                        productRepo::delete,
                        () -> {
                            throw new IllegalStateException("Producto no encontrado");
                        });
    }

    @Override
    public boolean validateProductName(String name, int id) {
        productRepo.findByName(name).ifPresent(existing -> {
            if (existing.getId() != id) {
                throw new ConflictException("Ya existe otro producto con el nombre: " + name);
            }
        });
        return true;
    }

    @Override
    public List<ProductResponseDto> findByUserId(Long userId) {
        // Verificar que el usuario existe
        userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado con ID: " + userId));

        return productRepo.findByOwnerId(userId)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Override
    public List<ProductResponseDto> findByCategoryId(Long categoryId) {
        validateCategory(categoryId);

        return productRepo.findByCategoriesId(categoryId)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

}
