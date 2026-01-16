package ec.edu.ups.icc.fundamentos01.categories.entity;

import java.util.HashSet;
import java.util.Set;

import ec.edu.ups.icc.fundamentos01.core.entities.BaseModel;
import ec.edu.ups.icc.fundamentos01.products.entities.ProductEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "categories")
public class CategoryEntity extends BaseModel {
    @Column(nullable = false, length = 100, unique = true)
    private String name;
    @Column(length = 500)
    private String description;

    @ManyToMany(mappedBy = "categories", fetch = FetchType.LAZY)
    private Set<ProductEntity> products = new HashSet<>();

    public CategoryEntity() {
    }

    public CategoryEntity(String name, String description) {
        this.name = name;
        this.description = description;
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

    public Set<ProductEntity> getProducts() {
        return products;
    }

    public void setProducts(Set<ProductEntity> products) {
        this.products = products != null ? products : new HashSet<>();
    }

    public void addProduct(ProductEntity product) {
        this.products.add(product);
    }

    public void removeProduct(ProductEntity product) {
        this.products.remove(product);
    }
}
