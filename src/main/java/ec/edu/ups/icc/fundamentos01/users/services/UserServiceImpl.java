package ec.edu.ups.icc.fundamentos01.users.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.products.mappers.ProductMapper;
import ec.edu.ups.icc.fundamentos01.products.models.Product;
import ec.edu.ups.icc.fundamentos01.products.repositories.ProductRepository;
import ec.edu.ups.icc.fundamentos01.users.dtos.CreateUserDto;
import ec.edu.ups.icc.fundamentos01.users.dtos.PartialUpdateUserDto;
import ec.edu.ups.icc.fundamentos01.users.dtos.UpdateUserDto;
import ec.edu.ups.icc.fundamentos01.users.dtos.UserResponseDto;
import ec.edu.ups.icc.fundamentos01.users.models.User;
import ec.edu.ups.icc.fundamentos01.users.mappers.UserMapper;
import ec.edu.ups.icc.fundamentos01.users.repositories.UserRepository;
import ec.edu.ups.icc.fundamentos01.exception.domain.NotFoundException;
import ec.edu.ups.icc.fundamentos01.exception.domain.ConflictException;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    private final ProductRepository productRepo;

    public UserServiceImpl(UserRepository userRepo, ProductRepository productRepo) {
        this.userRepo = userRepo;
        this.productRepo = productRepo;
    }

    @Override
    public List<UserResponseDto> findAll() {
        return userRepo.findAll()
                .stream()
                .map(User::fromEntity)
                .map(UserMapper::toResponse)
                .toList();
    }

    @Override
    public UserResponseDto findOne(int id) {
        return userRepo.findById((long) id)
                .map(User::fromEntity)
                .map(UserMapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
    }

    @Override
    public UserResponseDto create(CreateUserDto dto) {
        // Validar email unico.
        if (userRepo.findByEmail(dto.email).isPresent()) {
            throw new ConflictException("El email ya está registrado");
        }

        return Optional.of(dto)
                .map(UserMapper::fromCreateDto)
                .map(User::toEntity)
                .map(userRepo::save)
                .map(User::fromEntity)
                .map(UserMapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Error al crear el usuario"));
    }

    @Override
    public UserResponseDto update(int id, UpdateUserDto dto) {
        // Validar email unico.
        userRepo.findByEmail(dto.email).ifPresent(existing -> {
            if (existing.getId() != id) {
                throw new ConflictException("El email ya está registrado por otro usuario");
            }
        });

        return userRepo.findById((long) id)
                .map(User::fromEntity)
                .map(user -> user.update(dto))
                .map(User::toEntity)
                .map(userRepo::save)
                .map(User::fromEntity)
                .map(UserMapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
    }

    @Override
    public UserResponseDto partialUpdate(int id, PartialUpdateUserDto dto) {
        return userRepo.findById((long) id)
                .map(User::fromEntity)
                .map(user -> user.partialUpdate(dto))
                .map(User::toEntity)
                .map(userRepo::save)
                .map(User::fromEntity)
                .map(UserMapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
    }

    @Override
    public void delete(int id) {
        // Elimina si existe.
        userRepo.findById((long) id)
                .ifPresentOrElse(
                        userRepo::delete,
                        () -> {
                            throw new IllegalStateException("Usuario no encontrado");
                        });
    }

    @Override
    public List<ProductResponseDto> getProductsByUserId(Long userId) {
        // Consulta productos por ownerId usando ProductRepository.
        userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado con ID: " + userId));

        return productRepo.findByOwnerId(userId)
                .stream()
                .map(Product::fromEntity)
                .map(ProductMapper::toResponse)
                .toList();
    }

    @Override
    public List<ProductResponseDto> getProductsByUserIdWithFilters(Long id, String name, Double minPrice,
            Double maxPrice,
            Long categoryId) {
        userRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado con ID: " + id));

        // Obtener todos los productos del usuario
        List<ProductResponseDto> products = productRepo.findByOwnerId(id)
                .stream()
                .map(Product::fromEntity)
                .map(ProductMapper::toResponse)
                .toList();

        // Aplicar filtros manualmente
        return products.stream()
                .filter(p -> name == null || p.name.toLowerCase().contains(name.toLowerCase()))
                .filter(p -> minPrice == null || p.price >= minPrice)
                .filter(p -> maxPrice == null || p.price <= maxPrice)
                .filter(p -> categoryId == null || p.categories.stream().anyMatch(c -> c.id.equals(categoryId)))
                .toList();
    }
}
