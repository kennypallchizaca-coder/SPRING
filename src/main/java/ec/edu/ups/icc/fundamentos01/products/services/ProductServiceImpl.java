package ec.edu.ups.icc.fundamentos01.products.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import ec.edu.ups.icc.fundamentos01.products.dtos.CreateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.PartialUpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.UpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.entities.Product;
import ec.edu.ups.icc.fundamentos01.products.mappers.ProductMapper;


@Service
public class ProductServiceImpl implements ProductService {

    private List<Product> products = new ArrayList<>();
    private int currentId = 1;

    @Override
    public List<ProductResponseDto> findAll() {
        return products.stream().map(ProductMapper::toResponse).toList();
    }

    @Override
    public Object findOne(int id) {
        Product product = products.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);

        if (product == null) {
            return new Object() { public String error = "Product not found"; };
        }

        return ProductMapper.toResponse(product);
    }

    @Override
    public ProductResponseDto create(CreateProductDto dto) {
        Product product = ProductMapper.toEntity(currentId++, dto.name, dto.description, dto.price, dto.stock);
        products.add(product);
        return ProductMapper.toResponse(product);
    }

    @Override
    public Object update(int id, UpdateProductDto dto) {
        Product product = products.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
        if (product == null) return new Object() { public String error = "Product not found"; };

        product.setName(dto.name);
        product.setDescription(dto.description);
        product.setPrice(dto.price);
        product.setStock(dto.stock);

        return ProductMapper.toResponse(product);
    }

    @Override
    public Object partialUpdate(int id, PartialUpdateProductDto dto) {
        Product product = products.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
        if (product == null) return new Object() { public String error = "Product not found"; };

        if (dto.name != null) product.setName(dto.name);
        if (dto.description != null) product.setDescription(dto.description);
        if (dto.price != null) product.setPrice(dto.price);
        if (dto.stock != null) product.setStock(dto.stock);

        return ProductMapper.toResponse(product);
    }

    @Override
    public Object delete(int id) {
        boolean removed = products.removeIf(u -> u.getId() == id);
        if (!removed) return new Object() { public String error = "Product not found"; };

        return new Object() { public String message = "Deleted successfully"; };
    }
}