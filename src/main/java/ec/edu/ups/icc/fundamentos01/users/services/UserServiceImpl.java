package ec.edu.ups.icc.fundamentos01.users.services;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.products.mappers.ProductMapper;
import ec.edu.ups.icc.fundamentos01.products.models.Product;
import ec.edu.ups.icc.fundamentos01.products.repositories.ProductRepository;
import ec.edu.ups.icc.fundamentos01.security.models.RoleEntity;
import ec.edu.ups.icc.fundamentos01.security.models.RoleName;
import ec.edu.ups.icc.fundamentos01.security.repositories.RoleRepository;
import ec.edu.ups.icc.fundamentos01.users.dtos.CreateUserDto;
import ec.edu.ups.icc.fundamentos01.users.dtos.PartialUpdateUserDto;
import ec.edu.ups.icc.fundamentos01.users.dtos.UpdateUserDto;
import ec.edu.ups.icc.fundamentos01.users.dtos.UserResponseDto;
import ec.edu.ups.icc.fundamentos01.users.entities.UserEntity;
import ec.edu.ups.icc.fundamentos01.users.models.User;
import ec.edu.ups.icc.fundamentos01.users.mappers.UserMapper;
import ec.edu.ups.icc.fundamentos01.users.repositories.UserRepository;
import ec.edu.ups.icc.fundamentos01.exception.domain.NotFoundException;
import ec.edu.ups.icc.fundamentos01.exception.domain.ConflictException;
import ec.edu.ups.icc.fundamentos01.exception.domain.BadRequestException;

import java.util.List;
import java.util.HashSet;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    private final ProductRepository productRepo;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public UserServiceImpl(UserRepository userRepo,
            ProductRepository productRepo,
            PasswordEncoder passwordEncoder,
            RoleRepository roleRepository) {
        this.userRepo = userRepo;
        this.productRepo = productRepo;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
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
    public UserResponseDto findOne(Long id) {
        return userRepo.findById(id)
                .map(User::fromEntity)
                .map(UserMapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
    }

    @Override
    public UserResponseDto create(CreateUserDto dto) {
        // Validar email unico.
        if (userRepo.existsByEmail(dto.email)) {
            throw new ConflictException("El email ya está registrado");
        }

        return Optional.of(dto)
                .map(UserMapper::fromCreateDto)
                .map(user -> {
                    user.setPassword(passwordEncoder.encode(user.getPassword()));
                    return user;
                })
                .map(User::toEntity)
                .map(entity -> {
                    RoleEntity userRole = roleRepository.findByName(RoleName.ROLE_USER)
                            .orElseThrow(() -> new BadRequestException("Rol por defecto no encontrado"));
                    Set<RoleEntity> roles = new HashSet<>();
                    roles.add(userRole);
                    entity.setRoles(roles);
                    return entity;
                })
                .map(userRepo::save)
                .map(User::fromEntity)
                .map(UserMapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Error al crear el usuario"));
    }

    @Override
    public UserResponseDto update(Long id, UpdateUserDto dto) {
        // Validar email unico.
        userRepo.findByEmail(dto.email).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new ConflictException("El email ya está registrado por otro usuario");
            }
        });

        UserEntity entity = userRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        entity.setName(dto.name);
        entity.setEmail(dto.email);
        if (dto.password != null && !dto.password.isBlank()) {
            entity.setPassword(passwordEncoder.encode(dto.password));
        }

        return UserMapper.toResponse(User.fromEntity(userRepo.save(entity)));
    }

    @Override
    public UserResponseDto partialUpdate(Long id, PartialUpdateUserDto dto) {
        UserEntity entity = userRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        if (dto.name != null) {
            entity.setName(dto.name);
        }
        if (dto.email != null) {
            entity.setEmail(dto.email);
        }
        if (dto.password != null && !dto.password.isBlank()) {
            entity.setPassword(passwordEncoder.encode(dto.password));
        }

        return UserMapper.toResponse(User.fromEntity(userRepo.save(entity)));
    }

    @Override
    public void delete(Long id) {
        // Elimina si existe.
        userRepo.findById(id)
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
