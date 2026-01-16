package ec.edu.ups.icc.fundamentos01.products.repositories;

import ec.edu.ups.icc.fundamentos01.products.entities.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    // Métodos CRUD automáticos proporcionados por JpaRepository
    java.util.List<ProductEntity> findByNameContainingIgnoreCase(String name);

    Optional<ProductEntity> findByName(String name);

    java.util.List<ProductEntity> findByPriceLessThan(Double price);

    java.util.List<ProductEntity> findByStockGreaterThan(Integer stock);

    List<ProductEntity> findByOwnerId(Long userId);

    List<ProductEntity> findByOwnerName(String name);

    List<ProductEntity> findByCategoriesId(Long categoryId);

    List<ProductEntity> findByCategoriesName(String name);

    List<ProductEntity> findByCategoriesIdAndPriceGreaterThan(Long categoryId, Double price);


}
