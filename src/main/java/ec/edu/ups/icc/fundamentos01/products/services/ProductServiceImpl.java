package ec.edu.ups.icc.fundamentos01.products.services;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;

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
import ec.edu.ups.icc.fundamentos01.exception.domain.NotFoundException;
import ec.edu.ups.icc.fundamentos01.exception.domain.ConflictException;
import ec.edu.ups.icc.fundamentos01.exception.domain.BadRequestException;
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
    @Transactional(readOnly = true)
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
        Page<ProductEntity> productPage = productRepo.findAllWithRelations(pageable);
        
        return productPage.map(this::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Slice<ProductResponseDto> findAllSlice(int page, int size, String[] sort) {
        Pageable pageable = createPageable(page, size, sort);
        Slice<ProductEntity> productSlice = productRepo.findAllSliceWithRelations(pageable);
        
        return productSlice.map(this::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDto> findWithFilters(
            String name, Double minPrice, Double maxPrice, Long categoryId,
            int page, int size, String[] sort) {
        
        // Validaciones de filtros
        validateFilterParameters(minPrice, maxPrice);
        
        // Crear Pageable
        Pageable pageable = createPageable(page, size, sort);
        
        // Consulta con filtros y paginación
        Page<ProductEntity> productPage = productRepo.findWithFilters(
            name, minPrice, maxPrice, categoryId, pageable);
        
        return productPage.map(this::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDto> findByUserIdWithFilters(
            Long userId, String name, Double minPrice, Double maxPrice, Long categoryId,
            int page, int size, String[] sort) {
        
        // 1. Validar que el usuario existe
        if (!userRepo.existsById(userId)) {
            throw new NotFoundException("Usuario no encontrado con ID: " + userId);
        }
        
        // 2. Validar filtros
        validateFilterParameters(minPrice, maxPrice);
        
        // 3. Crear Pageable
        Pageable pageable = createPageable(page, size, sort);
        
        // 4. Consulta con filtros y paginación
        Page<ProductEntity> productPage = productRepo.findByOwnerIdWithFiltersPaginated(
            userId, name, minPrice, maxPrice, categoryId, pageable);
        
        return productPage.map(this::toResponseDto);
    }

    // ============== MÉTODOS HELPER ==============

    private Pageable createPageable(int page, int size, String[] sort) {
        // Validar parámetros
        if (page < 0) {
            throw new BadRequestException("La página debe ser mayor o igual a 0");
        }
        if (size < 1 || size > 100) {
            throw new BadRequestException("El tamaño debe estar entre 1 y 100");
        }
        
        // Crear Sort
        Sort sortObj = createSort(sort);
        
        return PageRequest.of(page, size, sortObj);
    }

    private Sort createSort(String[] sort) {
        if (sort == null || sort.length == 0) {
            return Sort.by("id");
        }

        List<Sort.Order> orders = new ArrayList<>();
        for (String sortParam : sort) {
            String[] parts = sortParam.split(",");
            String rawProperty = parts[0].trim();
            String property = normalizeSortProperty(rawProperty);
            String direction = parts.length > 1 ? parts[1].trim() : "asc";
            
            // Validar propiedades permitidas para evitar inyección SQL
            if (!isValidSortProperty(property)) {
                throw new BadRequestException("Propiedad de ordenamiento no válida: " + property);
            }
            
            Sort.Order order = "desc".equalsIgnoreCase(direction) 
                ? Sort.Order.desc(property)
                : Sort.Order.asc(property);
            
            orders.add(order);
        }
        
        return Sort.by(orders);
    }

    private boolean isValidSortProperty(String property) {
        // Lista blanca de propiedades permitidas para ordenamiento
        Set<String> allowedProperties = Set.of(
            "id", "name", "price", "stock", "createdAt", "updatedAt",
            "owner.name", "owner.email", "categories.name"
        );
        return allowedProperties.contains(property);
    }

    private String normalizeSortProperty(String property) {
        if (property == null) {
            return null;
        }
        if ("category.name".equals(property)) {
            return "categories.name";
        }
        return property;
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



}
