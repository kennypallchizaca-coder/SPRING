package ec.edu.ups.icc.fundamentos01.products.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import ec.edu.ups.icc.fundamentos01.products.dtos.CreateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.PartialUpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.UpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.models.Product;
import ec.edu.ups.icc.fundamentos01.products.mappers.ProductMapper;
import ec.edu.ups.icc.fundamentos01.products.repositories.ProductRepository;
import ec.edu.ups.icc.fundamentos01.exception.domain.NotFoundException;
import ec.edu.ups.icc.fundamentos01.exception.domain.ConflictException;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepo;

    public ProductServiceImpl(ProductRepository productRepo) {
        this.productRepo = productRepo;
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

    @Override
    public ProductResponseDto create(CreateProductDto dto) {
        // Validar que el nombre sea único
        if (productRepo.findByName(dto.name).isPresent()) {
            throw new ConflictException("Ya existe un producto con el nombre: " + dto.name);
        }

        return Optional.of(dto)
                .map(ProductMapper::fromCreateDto)
                .map(Product::toEntity)
                .map(productRepo::save)
                .map(Product::fromEntity)
                .map(ProductMapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Error al crear el producto"));
    }

    @Override
    public ProductResponseDto update(int id, UpdateProductDto dto) {
        // Validar que el nombre sea único (si cambió)
        productRepo.findByName(dto.name).ifPresent(existing -> {
            if (existing.getId() != id) {
                throw new ConflictException("Ya existe otro producto con el nombre: " + dto.name);
            }
        });

        return productRepo.findById((long) id)
                .map(Product::fromEntity)
                .map(product -> product.update(dto))
                .map(Product::toEntity)
                .map(productRepo::save)
                .map(Product::fromEntity)
                .map(ProductMapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado"));
    }

    @Override
    public ProductResponseDto partialUpdate(int id, PartialUpdateProductDto dto) {
        return productRepo.findById((long) id)
                .map(Product::fromEntity)
                .map(product -> product.partialUpdate(dto))
                .map(Product::toEntity)
                .map(productRepo::save)
                .map(Product::fromEntity)
                .map(ProductMapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado"));
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
}