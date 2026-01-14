package ec.edu.ups.icc.fundamentos01.products.services;

import java.util.List;

import org.springframework.stereotype.Service;

import ec.edu.ups.icc.fundamentos01.products.dtos.CreateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.PartialUpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.UpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.entities.ProductEntity;
import ec.edu.ups.icc.fundamentos01.products.models.Product;
import ec.edu.ups.icc.fundamentos01.products.mappers.ProductMapper;
import ec.edu.ups.icc.fundamentos01.products.repositories.ProductRepository;
import ec.edu.ups.icc.fundamentos01.users.entities.UserEntity;
import ec.edu.ups.icc.fundamentos01.users.repositories.UserRepository;
import ec.edu.ups.icc.fundamentos01.exception.domain.NotFoundException;
import ec.edu.ups.icc.fundamentos01.categories.entity.CategoryEntity;
import ec.edu.ups.icc.fundamentos01.categories.entity.CategoryResponseDto;
import ec.edu.ups.icc.fundamentos01.categories.repositories.CategoryRepository;
import ec.edu.ups.icc.fundamentos01.exception.domain.ConflictException;

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

    @Override
    public List<ProductResponseDto> findAll() {
        return productRepo.findAll()
                .stream()
                .map(Product::fromEntity)
                .map(ProductMapper::toResponse)
                .toList();
    }

    @Override
    public ProductResponseDto findOne(int id) {
        return productRepo.findById((long) id)
                .map(Product::fromEntity)
                .map(ProductMapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado"));
    }
    // CON UN USUARIO Y CON CATEGORIA
    // VALIDAR SU EXITENCIA

    @Override
    public ProductResponseDto create(CreateProductDto dto) {
        // Validar que el nombre sea único
        // if (productRepo.findByName(dto.name).isPresent()) {
        // throw new ConflictException("Ya existe un producto con el nombre: " +
        // dto.name);
        UserEntity owner = userRepo.findById(dto.userId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado con ID: " + dto.userId));

        CategoryEntity category = categoryRepo.findById(dto.categoryId)
                .orElseThrow(() -> new NotFoundException("Categoria no encontrada con ID: " + dto.categoryId));

        Product newProduct = Product.fromDto(dto);
        ProductEntity entity = newProduct.toEntity(owner, category);

        // PERSISTIR
        ProductEntity saved = productRepo.save(entity);

        // }

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

        // Crear objeto User anidado (corrected: user not userId)
        ProductResponseDto.UserSummaryDto userDto = new ProductResponseDto.UserSummaryDto();
        userDto.id = entity.getOwner().getId();
        userDto.name = entity.getOwner().getName();
        userDto.email = entity.getOwner().getEmail();
        dto.user = userDto; // CORREGIDO: user en lugar de userId

        // Crear objeto Category anidado (corrected: category not categoryId)
        CategoryResponseDto categoryDto = new CategoryResponseDto();
        categoryDto.id = entity.getCategory().getId();
        categoryDto.name = entity.getCategory().getName();
        categoryDto.description = entity.getCategory().getDescription();
        dto.category = categoryDto; // CORREGIDO: category en lugar de categoryId

        // Auditoría
        dto.createdAt = entity.getCreatedAt();
        dto.updatedAt = entity.getUpdatedAt();

        return dto;
    }

    @Override
    public ProductResponseDto update(int id, UpdateProductDto dto) {
        // Validar que el nombre sea único (si cambió)
        productRepo.findByName(dto.name).ifPresent(existing -> {
            if (existing.getId() != id) {
                throw new ConflictException("Ya existe otro producto con el nombre: " + dto.name);
            }
        });

        ProductEntity existingEntity = productRepo.findById((long) id)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado"));

        // Actualizar campos
        existingEntity.setName(dto.name);
        existingEntity.setDescription(dto.description);
        existingEntity.setPrice(dto.price);
        existingEntity.setStock(dto.stock);

        ProductEntity saved = productRepo.save(existingEntity);
        return toResponseDto(saved);
    }

    @Override
    public ProductResponseDto partialUpdate(int id, PartialUpdateProductDto dto) {
        ProductEntity existingEntity = productRepo.findById((long) id)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado"));

        // Actualizar solo los campos proporcionados
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
    public boolean secureUpdateProduct(String name, String description, double price) {
        productRepo.findByName(name).ifPresent(existing -> {
            if (price > 1000 && (description == null || description.isBlank())) {
                throw new ConflictException("Precio mayor a 1000 necesita justificación: " + name);
            }
        });
        return true;
    }

}