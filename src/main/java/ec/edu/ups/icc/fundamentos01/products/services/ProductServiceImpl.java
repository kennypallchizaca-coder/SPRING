package ec.edu.ups.icc.fundamentos01.products.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ec.edu.ups.icc.fundamentos01.categories.entity.CategoryEntity;
import ec.edu.ups.icc.fundamentos01.categories.entity.CategoryResponseDto;
import ec.edu.ups.icc.fundamentos01.categories.repositories.CategoryRepository;
import ec.edu.ups.icc.fundamentos01.products.dtos.CreateProductDto;
import ec.edu.ups.icc.fundamentos01.exception.domain.BadRequestException;
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

    private static final int MIN_PAGE_SIZE = 1;
    private static final int MAX_PAGE_SIZE = 100;

    private static final Set<String> ALLOWED_SORT_PROPERTIES = Set.of(
            "id",
            "name",
            "price",
            "stock",
            "createdAt",
            "updatedAt",
            "owner.name",
            "owner.email",
            "categories.name");

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

    // ============== MÉTODOS BÁSICOS ==============

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> findAll() {
        return productRepo.findAll()
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public List<ProductResponseDto> findByUserId(Long userId) {
        userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado con ID: " + userId));
        return productRepo.findByOwnerId(userId)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> findByCategoryId(Long categoryId) {
        validateCategory(categoryId);

        return productRepo.findByCategoriesId(categoryId)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    // ============== MÉTODOS CON PAGINACIÓN ==============

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDto> findAll(int page, int size, String[] sort) {
        Pageable pageable = createPageable(page, size, sort);
        return productRepo.findAll(pageable).map(this::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Slice<ProductResponseDto> findAllSlice(int page, int size, String[] sort) {
        Pageable pageable = createPageable(page, size, sort);
        return productRepo.findAllBy(pageable).map(this::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDto> findWithFilters(
            String name, Double minPrice, Double maxPrice, Long categoryId,
            int page, int size, String[] sort) {

        validateFilterParameters(minPrice, maxPrice);
        Pageable pageable = createPageable(page, size, sort);
        return productRepo.findWithFilters(name, minPrice, maxPrice, categoryId, pageable)
                .map(this::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDto> findByUserIdWithFilters(
            Long userId, String name, Double minPrice, Double maxPrice, Long categoryId,
            int page, int size, String[] sort) {

        userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado con ID: " + userId));
        validateFilterParameters(minPrice, maxPrice);
        Pageable pageable = createPageable(page, size, sort);
        return productRepo.findByUserIdWithFilters(userId, name, minPrice, maxPrice, categoryId, pageable)
                .map(this::toResponseDto);
    }

    // ============== MÉTODOS HELPER ==============

    private Pageable createPageable(int page, int size, String[] sort) {
        if (page < 0) {
            throw new BadRequestException("La página debe ser mayor o igual a 0");
        }
        if (size < MIN_PAGE_SIZE || size > MAX_PAGE_SIZE) {
            throw new BadRequestException(
                    "El tamaño debe estar entre " + MIN_PAGE_SIZE + " y " + MAX_PAGE_SIZE);
        }
        Sort sortDefinition = createSort(sort);
        return PageRequest.of(page, size, sortDefinition);
    }

    private Sort createSort(String[] sortParams) {
        if (sortParams == null || sortParams.length == 0) {
            return Sort.by("id");
        }

        List<Sort.Order> orders = new ArrayList<>();

        // Si tenemos exactamente 2 elementos y el segundo es "asc" o "desc",
        // probablemente Spring dividió "price,desc" en ["price", "desc"]
        if (sortParams.length == 2 &&
                (sortParams[1].equalsIgnoreCase("asc") || sortParams[1].equalsIgnoreCase("desc"))) {
            String property = sortParams[0].trim();
            String direction = sortParams[1].trim();

            if (!ALLOWED_SORT_PROPERTIES.contains(property)) {
                throw new BadRequestException("Propiedad de ordenamiento no válida: " + property);
            }

            Sort.Order order = "desc".equalsIgnoreCase(direction)
                    ? Sort.Order.desc(property)
                    : Sort.Order.asc(property);
            orders.add(order);
        } else {
            // Procesar cada parámetro normalmente
            for (String sortParam : sortParams) {
                String[] parts = sortParam.split(",");
                String property = parts[0].trim();
                String direction = parts.length > 1 ? parts[1].trim() : "asc";

                if (!ALLOWED_SORT_PROPERTIES.contains(property)) {
                    throw new BadRequestException("Propiedad de ordenamiento no válida: " + property);
                }

                Sort.Order order = "desc".equalsIgnoreCase(direction)
                        ? Sort.Order.desc(property)
                        : Sort.Order.asc(property);
                orders.add(order);
            }
        }

        return Sort.by(orders);
    }

    private void validateFilterParameters(Double minPrice, Double maxPrice) {
        if (minPrice != null && minPrice < 0) {
            throw new BadRequestException("El precio mínimo no puede ser negativo");
        }
        if (maxPrice != null && maxPrice < 0) {
            throw new BadRequestException("El precio máximo no puede ser negativo");
        }
        if (minPrice != null && maxPrice != null && maxPrice < minPrice) {
            throw new BadRequestException("El precio máximo debe ser mayor o igual al precio mínimo");
        }
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
}
