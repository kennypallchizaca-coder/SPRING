package ec.edu.ups.icc.fundamentos01.categories.models;

import ec.edu.ups.icc.fundamentos01.categories.entity.CreateCategoryDto;
import ec.edu.ups.icc.fundamentos01.categories.entity.UpdateCategoryDto;
import ec.edu.ups.icc.fundamentos01.categories.entity.CategoryEntity;

/**
 * Modelo de dominio para Category
 * Maneja reglas de negocio y validaciones
 */
public class Category {

    private Long id;
    private String name;
    private String description;

    // Constructores
    public Category() {
    }

    public Category(String name, String description) {
        this.validateBusinessRules(name);
        this.name = name;
        this.description = description;
    }

    private void validateBusinessRules(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la categoría es obligatorio");
        }
        if (name.length() < 3 || name.length() > 120) {
            throw new IllegalArgumentException("El nombre debe tener entre 3 y 120 caracteres");
        }
    }

    // ==================== FACTORY METHODS ====================

    /**
     * Crea un Category desde un DTO de creación
     */
    public static Category fromDto(CreateCategoryDto dto) {
        return new Category(dto.name, dto.description);
    }

    /**
     * Crea un Category desde una entidad persistente
     */
    public static Category fromEntity(CategoryEntity entity) {
        Category category = new Category(entity.getName(), entity.getDescription());
        category.id = entity.getId();
        return category;
    }

    // ==================== CONVERSION METHODS ====================

    /**
     * Convierte este Category a una entidad persistente
     */
    public CategoryEntity toEntity() {
        CategoryEntity entity = new CategoryEntity();

        if (this.id != null && this.id > 0) {
            entity.setId(this.id);
        }

        entity.setName(this.name);
        entity.setDescription(this.description);

        return entity;
    }

    /**
     * Actualiza los campos modificables de este Category
     */
    public Category update(UpdateCategoryDto dto) {
        this.validateBusinessRules(dto.name);
        this.name = dto.name;
        this.description = dto.description;
        return this;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
