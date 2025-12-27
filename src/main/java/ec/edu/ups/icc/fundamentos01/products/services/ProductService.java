package ec.edu.ups.icc.fundamentos01.products.services;

import java.util.List;

import ec.edu.ups.icc.fundamentos01.products.dtos.CreateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.PartialUpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.UpdateProductDto;

public interface ProductService {

    List<ProductResponseDto> findAll();

    Object findOne(int id);

    ProductResponseDto create(CreateProductDto dto);

    Object update(int id, UpdateProductDto dto);

    Object partialUpdate(int id, PartialUpdateProductDto dto);

    Object delete(int id);
}
