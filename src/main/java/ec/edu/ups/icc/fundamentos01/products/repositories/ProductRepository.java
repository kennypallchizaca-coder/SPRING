package ec.edu.ups.icc.fundamentos01.products.repositories;

import ec.edu.ups.icc.fundamentos01.products.entities.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    Slice<ProductEntity> findAllBy(Pageable pageable);

    @Query("SELECT DISTINCT p FROM ProductEntity p JOIN p.categories c WHERE p.owner.id = :ownerId"
            + " AND (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))"
            + " AND (:minPrice IS NULL OR p.price >= :minPrice)"
            + " AND (:maxPrice IS NULL OR p.price <= :maxPrice)"
            + " AND (:categoryId IS NULL OR c.id = :categoryId)")
    Page<ProductEntity> findByOwnerIdWithFilters(@Param("ownerId") Long ownerId, @Param("name") String name,
            @Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice,
            @Param("categoryId") Long categoryId, Pageable pageable);

    @Query("SELECT DISTINCT p FROM ProductEntity p LEFT JOIN p.categories c"
            + " WHERE (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))"
            + " AND (:minPrice IS NULL OR p.price >= :minPrice)"
            + " AND (:maxPrice IS NULL OR p.price <= :maxPrice)"
            + " AND (:categoryId IS NULL OR c.id = :categoryId)")
    Page<ProductEntity> findWithFilters(@Param("name") String name, @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice, @Param("categoryId") Long categoryId, Pageable pageable);

}
