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

    private final List<User> users = new ArrayList<>();
    private int currentId = 1;

    @GetMapping
    public List<UserResponseDto> findAll() {
        return users.stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public Object findOne(@PathVariable int id) {
        for (User user : users) {
            if (user.getId() == id) {
                return UserMapper.toResponse(user);
            }
        }
        return new Object() {
            public String error = "User not found";
        };
    }

    @PostMapping
    public UserResponseDto create(@RequestBody CreateUserDto dto) {
        User user = UserMapper.toEntity(currentId++, dto.name, dto.email);
        users.add(user);
        return UserMapper.toResponse(user);
    }

    @PutMapping("/{id}")
    public Object update(@PathVariable int id, @RequestBody UpdateUserDto dto) {
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
    }

    @PatchMapping("/{id}")
    public Object partialUpdate(@PathVariable int id, @RequestBody PartialUpdateUserDto dto) {
        for (User user : users) {
            if (user.getId() == id) {
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
    }

    @DeleteMapping("/{id}")
    public Object delete(@PathVariable int id) {
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
    }
}
