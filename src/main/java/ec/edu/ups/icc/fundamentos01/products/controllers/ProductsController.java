package ec.edu.ups.icc.fundamentos01.products.controllers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ec.edu.ups.icc.fundamentos01.products.dtos.CreateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.PartialUpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.UpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.entities.Product;
import ec.edu.ups.icc.fundamentos01.products.mappers.ProductMapper;

@RestController
@RequestMapping("/api/products")
public class ProductsController {

    private final List<Product> products = new ArrayList<>();
    private int currentId = 1;

    @GetMapping
    public List<ProductResponseDto> findAll() {
        return products.stream()
                .map(ProductMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public Object findOne(@PathVariable int id) {
        for (Product product : products) {
            if (product.getId() == id) {
                return ProductMapper.toResponse(product);
            }
        }
        return new Object() {
            public String error = "Product not found";
        };
    }

    @PostMapping
    public ProductResponseDto create(@RequestBody CreateProductDto dto) {
        Product product = ProductMapper.toEntity(currentId++, dto.name, dto.description, dto.price, dto.stock);
        products.add(product);
        return ProductMapper.toResponse(product);
    }

    @PutMapping("/{id}")
    public Object update(@PathVariable int id, @RequestBody UpdateProductDto dto) {
        for (Product product : products) {
            if (product.getId() == id) {
                product.setName(dto.name);
                product.setDescription(dto.description);
                product.setPrice(dto.price);
                product.setStock(dto.stock);
                return ProductMapper.toResponse(product);
            }
        }
        return new Object() {
            public String error = "Product not found";
        };
    }

    @PatchMapping("/{id}")
    public Object partialUpdate(@PathVariable int id, @RequestBody PartialUpdateProductDto dto) {
        for (Product product : products) {
            if (product.getId() == id) {
                if (dto.name != null) {
                    product.setName(dto.name);
                }
                if (dto.description != null) {
                    product.setDescription(dto.description);
                }
                if (dto.price != null) {
                    product.setPrice(dto.price);
                }
                if (dto.stock != null) {
                    product.setStock(dto.stock);
                }
                return ProductMapper.toResponse(product);
            }
        }
        return new Object() {
            public String error = "Product not found";
        };
    }

    @DeleteMapping("/{id}")
    public Object delete(@PathVariable int id) {
        Iterator<Product> iterator = products.iterator();
        while (iterator.hasNext()) {
            Product product = iterator.next();
            if (product.getId() == id) {
                iterator.remove();
                return new Object() {
                    public String message = "Deleted successfully";
                };
            }
        }
        return new Object() {
            public String error = "Product not found";
        };
    }
}
