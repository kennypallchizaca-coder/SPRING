package ec.edu.ups.icc.fundamentos01.products.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

import ec.edu.ups.icc.fundamentos01.products.dtos.CreateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.PartialUpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.UpdateProductDto;

public interface ProductService {

    // ============== MÉTODOS BÁSICOS EXISTENTES ==============

    ProductResponseDto findOne(Long id);

    ProductResponseDto create(CreateProductDto dto);

    ProductResponseDto update(Long id, UpdateProductDto dto);

    ProductResponseDto partialUpdate(Long id, PartialUpdateProductDto dto);

    void delete(Long id);

    boolean validateProductName(String name, int id);

    List<ProductResponseDto> findAll();

    List<ProductResponseDto> findByUserId(Long userId);

    List<ProductResponseDto> findByCategoryId(Long categoryId);

    // ============== MÉTODOS CON PAGINACIÓN ==============

    /**
     * Obtiene todos los productos con paginación completa (Page)
     */
    Page<ProductResponseDto> findAll(int page, int size, String[] sort);

    /**
     * Obtiene todos los productos con paginación ligera (Slice)
     */
    Slice<ProductResponseDto> findAllSlice(int page, int size, String[] sort);

    /**
     * Busca productos con filtros y paginación
     */
    Page<ProductResponseDto> findWithFilters(
            String name,
            Double minPrice,
            Double maxPrice,
            Long categoryId,
            int page,
            int size,
            String[] sort);

    /**
     * Productos de un usuario con filtros y paginación
     */
    Page<ProductResponseDto> findByUserIdWithFilters(
            Long userId,
            String name,
            Double minPrice,
            Double maxPrice,
            Long categoryId,
            int page,
            int size,
            String[] sort);
}
