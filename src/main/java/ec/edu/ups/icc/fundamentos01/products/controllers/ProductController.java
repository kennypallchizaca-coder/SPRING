package ec.edu.ups.icc.fundamentos01.products.controllers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ec.edu.ups.icc.fundamentos01.products.dtos.CreateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.PartialUpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.UpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ValidateProductNameDto;
import ec.edu.ups.icc.fundamentos01.products.services.ProductService;
import ec.edu.ups.icc.fundamentos01.security.services.UserDetailsImpl;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // ============== ENDPOINTS DE CREACIÓN ==============

    /**
     * Crear producto
     * POST /api/products
     * 
     * Nota: Requiere autenticación por .anyRequest().authenticated()
     * Se asigna al usuario actual como owner en el servicio
     */
    @PostMapping
    public ResponseEntity<ProductResponseDto> create(@Valid @RequestBody CreateProductDto dto) {
        ProductResponseDto created = productService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ============== ENDPOINTS DE CONSULTA ==============

    /**
     * Listar TODOS los productos (sin paginación) - SOLO ADMIN
     * GET /api/products
     * 
     * Este endpoint muestra información sensible de todos los usuarios
     * Por eso está protegido con @PreAuthorize
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProductResponseDto>> findAll() {
        List<ProductResponseDto> products = productService.findAll();
        return ResponseEntity.ok(products);
    }

    /**
     * Listar productos con paginación básica
     * GET /api/products/paginated?page=0&size=10&sort=name,asc
     * 
     * Nota: Requiere autenticación por .anyRequest().authenticated()
     */
    @GetMapping("/paginated")
    public ResponseEntity<Page<ProductResponseDto>> findAllPaginado(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "id") String[] sort) {

        Page<ProductResponseDto> products = productService.findAll(page, size, sort);
        return ResponseEntity.ok(products);
    }

    /**
     * Listar productos usando Slice para mejor performance
     * GET /api/products/slice?page=0&size=10&sort=createdAt,desc
     * 
     * Nota: Requiere autenticación por .anyRequest().authenticated()
     */
    @GetMapping("/slice")
    public ResponseEntity<Slice<ProductResponseDto>> findAllSlice(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "id") String[] sort) {

        Slice<ProductResponseDto> products = productService.findAllSlice(page, size, sort);
        return ResponseEntity.ok(products);
    }

    /**
     * Listar productos con filtros opcionales y paginación
     * GET /api/products/search?name=laptop&minPrice=500&page=0&size=5
     * 
     * Nota: Requiere autenticación por .anyRequest().authenticated()
     */
    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponseDto>> findWithFilters(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "minPrice", required = false) Double minPrice,
            @RequestParam(value = "maxPrice", required = false) Double maxPrice,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "id") String[] sort) {

        Page<ProductResponseDto> products = productService.findWithFilters(
                name, minPrice, maxPrice, categoryId, page, size, sort);

        return ResponseEntity.ok(products);
    }

    /**
     * Obtener producto por ID
     * GET /api/products/{id}
     * 
     * Nota: Requiere autenticación por .anyRequest().authenticated()
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> findById(@PathVariable("id") String id) {
        ProductResponseDto product = productService.findOne(Long.parseLong(id));
        return ResponseEntity.ok(product);
    }

    /**
     * Productos de un usuario específico con filtros opcionales y paginación
     * GET /api/products/user/1?name=laptop&page=0&size=5&sort=price,desc
     * 
     * Nota: Requiere autenticación por .anyRequest().authenticated()
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<ProductResponseDto>> findByUserId(
            @PathVariable("userId") Long userId,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "minPrice", required = false) Double minPrice,
            @RequestParam(value = "maxPrice", required = false) Double maxPrice,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "id") String[] sort) {

        Page<ProductResponseDto> products = productService.findByUserIdWithFilters(
                userId, name, minPrice, maxPrice, categoryId, page, size, sort);

        return ResponseEntity.ok(products);
    }

    /**
     * Productos por categoría
     * GET /api/products/category/{categoryId}
     * 
     * Nota: Requiere autenticación por .anyRequest().authenticated()
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductResponseDto>> findByCategoryId(
            @PathVariable("categoryId") Long categoryId) {
        List<ProductResponseDto> products = productService.findByCategoryId(categoryId);
        return ResponseEntity.ok(products);
    }

    // ============== ENDPOINTS DE MODIFICACIÓN ==============

    /**
     * Actualizar producto
     * PUT /api/products/{id}
     * 
     * Nota: NO tiene @PreAuthorize aquí porque la validación de ownership
     * se hace EN EL SERVICIO (ver Práctica 13)
     * 
     * El servicio valida:
     * - Si eres USER → Solo puedes actualizar TUS productos
     * - Si eres ADMIN o MODERATOR → Puedes actualizar CUALQUIER producto
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> update(
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateProductDto dto,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        ProductResponseDto updated = productService.update(id, dto, currentUser);
        return ResponseEntity.ok(updated);
    }

    /**
     * Eliminar producto
     * DELETE /api/products/{id}
     * 
     * Nota: NO tiene @PreAuthorize aquí porque la validación de ownership
     * se hace EN EL SERVICIO (ver Práctica 13)
     * 
     * El servicio valida:
     * - Si eres USER → Solo puedes eliminar TUS productos
     * - Si eres ADMIN o MODERATOR → Puedes eliminar CUALQUIER producto
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        productService.delete(id, currentUser);
        return ResponseEntity.noContent().build();
    }

    // ============== ENDPOINTS ADICIONALES (EXISTENTES) ==============

    @PatchMapping("/{id}")
    public ProductResponseDto partialUpdate(@PathVariable("id") Long id,
            @Valid @RequestBody PartialUpdateProductDto dto) {
        return productService.partialUpdate(id, dto);
    }

    @PostMapping("/validate-name")
    public ResponseEntity<Boolean> validateProductName(@Valid @RequestBody ValidateProductNameDto dto) {
        productService.validateProductName(dto.name, dto.id);
        return ResponseEntity.ok().body(true);
    }

    @GetMapping("/user/{userId}/all")
    public List<ProductResponseDto> findByUserIdList(@PathVariable("userId") Long userId) {
        return productService.findByUserId(userId);
    }
}
