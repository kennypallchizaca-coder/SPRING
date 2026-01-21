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

       // ============== CONSULTAS BÁSICAS (SIN PAGINACIÓN) ==============

       List<ProductEntity> findByNameContainingIgnoreCase(String name);

       Optional<ProductEntity> findByName(String name);

       List<ProductEntity> findByPriceLessThan(Double price);

       List<ProductEntity> findByStockGreaterThan(Integer stock);

       List<ProductEntity> findByOwnerId(Long userId);

       List<ProductEntity> findByOwnerName(String name);

       List<ProductEntity> findByCategoriesId(Long categoryId);

       List<ProductEntity> findByCategoriesName(String name);

       List<ProductEntity> findByCategoriesIdAndPriceGreaterThan(Long categoryId, Double price);

       // ============== CONSULTAS CON PAGINACIÓN ==============

       /**
        * Busca todos los productos con Slice (sin count total)
        */
       Slice<ProductEntity> findAllBy(Pageable pageable);

       /**
        * Busca productos por nombre de usuario con paginación
        */
       @Query("SELECT DISTINCT p FROM ProductEntity p " +
                     "JOIN p.owner o " +
                     "WHERE LOWER(o.name) LIKE LOWER(CONCAT('%', :ownerName, '%'))")
       Page<ProductEntity> findByOwnerNameContaining(@Param("ownerName") String ownerName, Pageable pageable);

       /**
        * Busca productos por categoría con paginación
        * Usa LEFT JOIN porque la relación es Many-to-Many
        */
       @Query("SELECT DISTINCT p FROM ProductEntity p " +
                     "LEFT JOIN p.categories c " +
                     "WHERE c.id = :categoryId")
       Page<ProductEntity> findByCategoriesId(@Param("categoryId") Long categoryId, Pageable pageable);

       /**
        * Busca productos en rango de precio con paginación
        */
       @Query("SELECT DISTINCT p FROM ProductEntity p " +
                     "WHERE p.price BETWEEN :minPrice AND :maxPrice")
       Page<ProductEntity> findByPriceBetween(@Param("minPrice") Double minPrice,
                     @Param("maxPrice") Double maxPrice,
                     Pageable pageable);

       /**
        * Busca productos con filtros opcionales y paginación
        * Todos los parámetros son opcionales excepto el Pageable
        * NOTA: Usa LEFT JOIN p.categories para relación Many-to-Many
        */
       @Query("SELECT DISTINCT p FROM ProductEntity p " +
                     "LEFT JOIN p.categories c " +
                     "WHERE (COALESCE(:name, '') = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
                     "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
                     "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
                     "AND (:categoryId IS NULL OR c.id = :categoryId)")
       Page<ProductEntity> findWithFilters(
                     @Param("name") String name,
                     @Param("minPrice") Double minPrice,
                     @Param("maxPrice") Double maxPrice,
                     @Param("categoryId") Long categoryId,
                     Pageable pageable);

       /**
        * Busca productos de un usuario con filtros opcionales y paginación
        * NOTA: Usa LEFT JOIN p.categories para relación Many-to-Many
        */
       @Query("SELECT DISTINCT p FROM ProductEntity p " +
                     "LEFT JOIN p.categories c " +
                     "WHERE p.owner.id = :userId " +
                     "AND (COALESCE(:name, '') = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
                     "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
                     "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
                     "AND (:categoryId IS NULL OR c.id = :categoryId)")
       Page<ProductEntity> findByUserIdWithFilters(
                     @Param("userId") Long userId,
                     @Param("name") String name,
                     @Param("minPrice") Double minPrice,
                     @Param("maxPrice") Double maxPrice,
                     @Param("categoryId") Long categoryId,
                     Pageable pageable);

       // ============== CONSULTAS CON SLICE PARA PERFORMANCE ==============

       /**
        * Productos de una categoría usando Slice
        * Usa LEFT JOIN para relación Many-to-Many
        */
       @Query("SELECT DISTINCT p FROM ProductEntity p " +
                     "LEFT JOIN p.categories c " +
                     "WHERE c.id = :categoryId " +
                     "ORDER BY p.createdAt DESC")
       Slice<ProductEntity> findByCategoriesIdOrderByCreatedAtDesc(@Param("categoryId") Long categoryId,
                     Pageable pageable);

       /**
        * Productos ordenados por fecha de creación usando Slice
        */
       @Query("SELECT DISTINCT p FROM ProductEntity p " +
                     "ORDER BY p.createdAt DESC")
       Slice<ProductEntity> findAllOrderByCreatedAtDesc(Pageable pageable);

       // ============== CONSULTAS DE CONTEO (PARA METADATOS) ==============

       /**
        * Cuenta productos con filtros (útil para estadísticas)
        * NOTA: Usa COUNT(DISTINCT p.id) por la relación Many-to-Many
        */
       @Query("SELECT COUNT(DISTINCT p.id) FROM ProductEntity p " +
                     "LEFT JOIN p.categories c " +
                     "WHERE (COALESCE(:name, '') = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
                     "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
                     "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
                     "AND (:categoryId IS NULL OR c.id = :categoryId)")
       long countWithFilters(
                     @Param("name") String name,
                     @Param("minPrice") Double minPrice,
                     @Param("maxPrice") Double maxPrice,
                     @Param("categoryId") Long categoryId);
}
