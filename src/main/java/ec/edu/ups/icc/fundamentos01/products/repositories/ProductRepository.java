package ec.edu.ups.icc.fundamentos01.products.repositories;

import ec.edu.ups.icc.fundamentos01.products.entities.ProductEntity;
import ec.edu.ups.icc.fundamentos01.users.dtos.UserResponseDto;
import ec.edu.ups.icc.fundamentos01.users.entities.UserEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    
    // ============== MÉTODOS BÁSICOS EXISTENTES ==============
    // Métodos CRUD automáticos proporcionados por JpaRepository
    
    /**
     * Encuentra todos los productos con sus relaciones cargadas (para paginación)
     */
    @Query(value = "SELECT DISTINCT p FROM ProductEntity p " +
           "LEFT JOIN FETCH p.owner " +
           "LEFT JOIN p.categories categories",
           countQuery = "SELECT COUNT(DISTINCT p) FROM ProductEntity p")
    Page<ProductEntity> findAllWithRelations(Pageable pageable);
    
    @Query("SELECT DISTINCT p FROM ProductEntity p " +
           "LEFT JOIN FETCH p.owner " +
           "LEFT JOIN p.categories categories")
    Slice<ProductEntity> findAllSliceWithRelations(Pageable pageable);
    
    java.util.List<ProductEntity> findByNameContainingIgnoreCase(String name);

    Optional<ProductEntity> findByName(String name);

    java.util.List<ProductEntity> findByPriceLessThan(Double price);

    java.util.List<ProductEntity> findByStockGreaterThan(Integer stock);

    List<ProductEntity> findByOwnerId(Long userId);

    List<ProductEntity> findByOwnerName(String name);

    List<ProductEntity> findByCategoriesId(Long categoryId);

    List<ProductEntity> findByCategoriesName(String name);

    List<ProductEntity> findByCategoriesIdAndPriceGreaterThan(Long categoryId, Double price);

    Optional<UserEntity> findByNameAndOwnerId(String name, Long ownerId);

    @Query("SELECT DISTINCT p FROM ProductEntity p JOIN p.categories c WHERE p.owner.id = :ownerId"
            + " AND (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))"
               + " AND (:minPrice IS NULL OR p.price >= :minPrice)"
            + " AND (:maxPrice IS NULL OR p.price <= :maxPrice)"
            + " AND (:categoryId IS NULL OR c.id = :categoryId)")
    List<ProductEntity> findByOwnerIdWithFilters(@Param("ownerId") Long ownerId, @Param("name") String name,
            @Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice,
            @Param("categoryId") Long categoryId);

    // ============== CONSULTAS PERSONALIZADAS CON PAGINACIÓN ==============

    /**
     * Busca productos por nombre de usuario con paginación
     */
    @Query("SELECT DISTINCT p FROM ProductEntity p " +
           "LEFT JOIN FETCH p.owner o " +
           "LEFT JOIN p.categories categories " +
           "WHERE LOWER(o.name) LIKE LOWER(CONCAT('%', :ownerName, '%'))")
    Page<ProductEntity> findByOwnerNameContaining(@Param("ownerName") String ownerName, Pageable pageable);

    /**
     * Busca productos por categoría con paginación
     */
    @Query("SELECT DISTINCT p FROM ProductEntity p " +
           "LEFT JOIN FETCH p.owner " +
           "LEFT JOIN p.categories categories " +
           "WHERE categories.id = :categoryId")
    Page<ProductEntity> findByCategoriesId(@Param("categoryId") Long categoryId, Pageable pageable);

    /**
     * Busca productos en rango de precio con paginación
     */
    @Query("SELECT DISTINCT p FROM ProductEntity p " +
           "LEFT JOIN FETCH p.owner " +
           "LEFT JOIN p.categories categories " +
           "WHERE p.price BETWEEN :minPrice AND :maxPrice")
    Page<ProductEntity> findByPriceBetween(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice, Pageable pageable);

    // ============== CONSULTA COMPLEJA CON FILTROS Y PAGINACIÓN ==============

    /**
     * Busca productos con filtros opcionales y paginación
     * Todos los parámetros son opcionales excepto el Pageable
     */
    @Query(value = "SELECT DISTINCT p FROM ProductEntity p " +
           "LEFT JOIN FETCH p.owner o " +
           "LEFT JOIN p.categories categories " +
           "WHERE (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
           "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
           "AND (:categoryId IS NULL OR categories.id = :categoryId)",
           countQuery = "SELECT COUNT(DISTINCT p) FROM ProductEntity p " +
           "LEFT JOIN p.categories categories " +
           "WHERE (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
           "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
           "AND (:categoryId IS NULL OR categories.id = :categoryId)")
    Page<ProductEntity> findWithFilters(
        @Param("name") String name,
        @Param("minPrice") Double minPrice,
        @Param("maxPrice") Double maxPrice,
        @Param("categoryId") Long categoryId,
        Pageable pageable
    );

    /**
     * Busca productos de un usuario con filtros opcionales y paginación
     */
    @Query(value = "SELECT DISTINCT p FROM ProductEntity p " +
           "JOIN FETCH p.owner o " +
           "LEFT JOIN p.categories categories " +
           "WHERE o.id = :userId " +
           "AND (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
           "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
           "AND (:categoryId IS NULL OR categories.id = :categoryId)",
           countQuery = "SELECT COUNT(DISTINCT p) FROM ProductEntity p " +
           "JOIN p.owner o " +
           "LEFT JOIN p.categories categories " +
           "WHERE o.id = :userId " +
           "AND (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
           "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
           "AND (:categoryId IS NULL OR categories.id = :categoryId)")
    Page<ProductEntity> findByOwnerIdWithFiltersPaginated(
        @Param("userId") Long userId,
        @Param("name") String name,
        @Param("minPrice") Double minPrice,
        @Param("maxPrice") Double maxPrice,
        @Param("categoryId") Long categoryId,
        Pageable pageable
    );

    // ============== CONSULTAS CON SLICE PARA PERFORMANCE ==============

    /**
     * Productos de una categoría usando Slice
     */
    @Query("SELECT DISTINCT p FROM ProductEntity p " +
           "LEFT JOIN FETCH p.owner " +
           "LEFT JOIN p.categories categories " +
           "WHERE categories.id = :categoryId " +
           "ORDER BY p.createdAt DESC")
    Slice<ProductEntity> findByCategoriesIdOrderByCreatedAtDesc(@Param("categoryId") Long categoryId, Pageable pageable);

    /**
     * Productos ordenados por fecha de creación usando Slice
     */
    @Query("SELECT DISTINCT p FROM ProductEntity p " +
           "LEFT JOIN FETCH p.owner " +
           "LEFT JOIN p.categories categories " +
           "ORDER BY p.createdAt DESC")
    Slice<ProductEntity> findAllOrderByCreatedAtDesc(Pageable pageable);

    // ============== CONSULTAS DE CONTEO (PARA METADATOS) ==============

    /**
     * Cuenta productos con filtros (útil para estadísticas)
     */
    @Query("SELECT COUNT(DISTINCT p) FROM ProductEntity p " +
           "LEFT JOIN p.owner o " +
           "LEFT JOIN p.categories c " +
           "WHERE (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
           "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
           "AND (:categoryId IS NULL OR c.id = :categoryId)")
    long countWithFilters(
        @Param("name") String name,
        @Param("minPrice") Double minPrice,
        @Param("maxPrice") Double maxPrice,
        @Param("categoryId") Long categoryId
    );
}
