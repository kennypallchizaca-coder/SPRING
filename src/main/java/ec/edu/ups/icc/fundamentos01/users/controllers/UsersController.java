package ec.edu.ups.icc.fundamentos01.users.controllers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ec.edu.ups.icc.fundamentos01.users.dtos.CreateUserDto;
import ec.edu.ups.icc.fundamentos01.users.dtos.PartialUpdateUserDto;
import ec.edu.ups.icc.fundamentos01.users.dtos.UpdateUserDto;
import ec.edu.ups.icc.fundamentos01.users.dtos.UserResponseDto;
import ec.edu.ups.icc.fundamentos01.users.entities.User;
import ec.edu.ups.icc.fundamentos01.users.mappers.UserMapper;


@RestController
@RequestMapping("/api/users")
public class UsersController {

    // Lista que simula una base de datos en memoria
    private List<User> users = new ArrayList<>();

    // ID secuencial para nuevos usuarios
    private int currentId = 1;

    /**
     * GET /api/users
     * Retorna la lista de usuarios como DTOs de respuesta.
     */
    @GetMapping
    public List<UserResponseDto> findAll() {

        // Programación tradicional (iterativa)
        /*
        List<UserResponseDto> dtos = new ArrayList<>();
        for (User user : users) {
            dtos.add(UserMapper.toResponse(user));
        }
        return dtos;
        */

        // Programación funcional (Streams)
        return users.stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public Object findOne(@PathVariable int id) {

        // Programación tradicional (búsqueda lineal)
        for (User user : users) {
            if (user.getId() == id) {
                return UserMapper.toResponse(user);
            }
        }
        return new Object() {
            public String error = "User not found";
        };

        /*
        // Programación funcional (Streams)
        return users.stream()
                .filter(u -> u.getId() == id)
                .findFirst()
                .map(UserMapper::toResponse)
                .orElseGet(() -> new Object() {
                    public String error = "User not found";
                });
        */
    }


    @PostMapping
    public UserResponseDto create(@RequestBody CreateUserDto dto) {

        // Se crea la entidad User desde el DTO
        User user = UserMapper.toEntity(currentId++, dto.name, dto.email);

        // Se guarda en la lista en memoria
        users.add(user);

        // Se retorna el DTO de respuesta
        return UserMapper.toResponse(user);
    }

    @PutMapping("/{id}")
    public Object update(@PathVariable int id, @RequestBody UpdateUserDto dto) {

        // Programación tradicional (iterativa)
        for (User user : users) {
            if (user.getId() == id) {
                user.setName(dto.name);
                user.setEmail(dto.email);
                return UserMapper.toResponse(user);
            }
        }
        return new Object() {
            public String error = "User not found";
        };

        /*
        // Programación funcional
        User user = users.stream()
                .filter(u -> u.getId() == id)
                .findFirst()
                .orElse(null);

        if (user == null) {
            return new Object() { public String error = "User not found"; };
        }

        user.setName(dto.name);
        user.setEmail(dto.email);

        return UserMapper.toResponse(user);
        */
    }

    @PatchMapping("/{id}")
    public Object partialUpdate(@PathVariable int id, @RequestBody PartialUpdateUserDto dto) {

        // Programación tradicional (iterativa)
        for (User user : users) {
            if (user.getId() == id) {

                // Solo se actualizan los campos enviados
                if (dto.name != null) {
                    user.setName(dto.name);
                }
                if (dto.email != null) {
                    user.setEmail(dto.email);
                }

                return UserMapper.toResponse(user);
            }
        }
        return new Object() {
            public String error = "User not found";
        };

        /*
        // Programación funcional
        User user = users.stream()
                .filter(u -> u.getId() == id)
                .findFirst()
                .orElse(null);

        if (user == null) {
            return new Object() { public String error = "User not found"; };
        }

        if (dto.name != null) user.setName(dto.name);
        if (dto.email != null) user.setEmail(dto.email);

        return UserMapper.toResponse(user);
        */
    }

    @DeleteMapping("/{id}")
    public Object delete(@PathVariable int id) {

        // Programación tradicional (Iterator)
        Iterator<User> iterator = users.iterator();
        while (iterator.hasNext()) {
            User user = iterator.next();
            if (user.getId() == id) {
                iterator.remove();
                return new Object() {
                    public String message = "Deleted successfully";
                };
            }
        }
        return new Object() {
            public String error = "User not found";
        };

        /*
        // Programación funcional
        boolean exists = users.removeIf(u -> u.getId() == id);
        if (!exists) {
            return new Object() { public String error = "User not found"; };
        }
        return new Object() { public String message = "Deleted successfully"; };
        */
    }
}
