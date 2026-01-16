package ec.edu.ups.icc.fundamentos01.products.models;

import ec.edu.ups.icc.fundamentos01.categories.entity.CategoryEntity;
import ec.edu.ups.icc.fundamentos01.products.dtos.CreateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.PartialUpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.UpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.entities.ProductEntity;
import ec.edu.ups.icc.fundamentos01.users.entities.UserEntity;

public class Product {

    private long id;
    private String name;
    private String description;
    private double price;
    private Integer stock;

    public Product(long id, String name, String description, double price, Integer stock) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
    }

    public Product(String name, String description, double price, Integer stock) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
    }

    // Getters y Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    // ==================== FACTORY METHODS ====================

    public static Product fromDto(CreateProductDto dto) {
        return new Product(dto.name, dto.description, dto.price, dto.stock);
    }

    /**
     * Crea un Product desde un DTO de creación
     *
     * @param dto DTO con datos del formulario
     * @return instancia de Product para lógica de negocio
     */
    // public static Product fromDto(CreateProductDto dto) {
    // return new Product(0, dto.name, dto.description, dto.price, dto.stock);
    // }

    /**
     * Crea un Product desde una entidad persistente
     *
     * @param entity Entidad recuperada de la BD
     * @return instancia de Product para lógica de negocio
     */
    public static Product fromEntity(ProductEntity entity) {
        return new Product(
                entity.getId().intValue(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getStock());
    }

    // ==================== CONVERSION METHODS ====================

    /**
     * Convierte este Product a una entidad persistente
     *
     * @return ProductEntity lista para guardar en BD
     */
    public ProductEntity toEntity(UserEntity owner) {
        ProductEntity entity = new ProductEntity();
        if (this.id > 0) {
            entity.setId((long) this.id);
        }
        entity.setName(this.name);
        entity.setDescription(this.description);
        entity.setPrice(this.price);
        entity.setStock(this.stock);
        return entity;
    }

    public ProductEntity toEntity(UserEntity owner, CategoryEntity categoryEntity) {
        ProductEntity entity = new ProductEntity();
        if (this.id > 0) {
            entity.setId((long) this.id);
        }
        entity.setName(this.name);
        entity.setDescription(this.description);
        entity.setPrice(this.price);
        entity.setStock(this.stock);
        entity.setOwner(owner);
        entity.setCategory(categoryEntity);
        return entity;
    }

    /**
     * Convierte este Product a un DTO de respuesta
     *
     * @return DTO sin información sensible
     */
    public ProductResponseDto toResponseDto() {
        ProductResponseDto dto = new ProductResponseDto();
        dto.name = this.name;
        dto.description = this.description;
        dto.price = this.price;
        dto.stock = this.stock;
        return dto;
    }

    /**
     * Aplica actualización completa desde UpdateProductDto
     *
     * @param dto DTO con campos a actualizar
     * @return this para encadenamiento
     */
    public Product update(UpdateProductDto dto) {
        this.name = dto.name;
        this.description = dto.description;
        this.price = dto.price;
        this.stock = dto.stock;
        return this;
    }

    /**
     * Aplica actualización parcial desde PartialUpdateProductDto
     *
     * @param dto DTO con campos opcionales a actualizar
     * @return this para encadenamiento
     */
    public Product partialUpdate(PartialUpdateProductDto dto) {
        if (dto.name != null) {
            this.name = dto.name;
        }
        if (dto.description != null) {
            this.description = dto.description;
        }
        if (dto.price != null) {
            this.price = dto.price;
        }
        if (dto.stock != null) {
            this.stock = dto.stock;
        }
        return this;
    }
}
