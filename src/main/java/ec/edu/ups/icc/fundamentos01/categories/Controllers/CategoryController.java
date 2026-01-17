package ec.edu.ups.icc.fundamentos01.categories.Controllers;

import ec.edu.ups.icc.fundamentos01.categories.Services.CategoryService;
import ec.edu.ups.icc.fundamentos01.categories.entity.CreateCategoryDto;
import ec.edu.ups.icc.fundamentos01.categories.entity.UpdateCategoryDto;
import ec.edu.ups.icc.fundamentos01.categories.entity.CategoryResponseDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> findAll() {
        List<CategoryResponseDto> categories = categoryService.findAll();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> findById(@PathVariable("id") Long id) {
        CategoryResponseDto category = categoryService.findOne(id);
        return ResponseEntity.ok(category);
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDto> create(@Valid @RequestBody CreateCategoryDto dto) {
        CategoryResponseDto created = categoryService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> update(
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateCategoryDto dto) {
        CategoryResponseDto updated = categoryService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/categories/{id}/products/count: cuenta productos por categoria.
    @GetMapping("/{id}/products/count")
    public ResponseEntity<Long> countProductsByCategoryId(@PathVariable("id") Long id) {
        Long count = categoryService.countProductsByCategoryId(id);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/{id}/products")
    public ResponseEntity<List<ProductResponseDto>> findProductsByCategoryId(@PathVariable("id") Long id) {
        List<ProductResponseDto> products = categoryService.getProductsByCategoryId(id);
        return ResponseEntity.ok(products);
    }
}
