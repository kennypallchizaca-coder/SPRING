package ec.edu.ups.icc.fundamentos01.users.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import ec.edu.ups.icc.fundamentos01.users.dtos.CreateUserDto;
import ec.edu.ups.icc.fundamentos01.users.dtos.PartialUpdateUserDto;
import ec.edu.ups.icc.fundamentos01.users.dtos.UpdateUserDto;
import ec.edu.ups.icc.fundamentos01.users.dtos.UserResponseDto;
import ec.edu.ups.icc.fundamentos01.users.entities.User;
import ec.edu.ups.icc.fundamentos01.users.mappers.UserMapper;

@Service
public class UserServiceImpl implements UserService {

    private List<User> users = new ArrayList<>();
    private int currentId = 1;

    @Override
    public List<UserResponseDto> findAll() {
        return users.stream().map(UserMapper::toResponse).toList();
    }

    @Override
    public Object findOne(int id) {
        User user = users.stream()
                .filter(u -> u.getId() == id)
                .findFirst()
                .orElse(null);

        if (user == null) {
            return new Object() { public String error = "User not found"; };
        }

        return UserMapper.toResponse(user);
    }

    @Override
    public UserResponseDto create(CreateUserDto dto) {
        User user = UserMapper.toEntity(currentId++, dto.name, dto.email);
        users.add(user);
        return UserMapper.toResponse(user);
    }

    @Override
    public Object update(int id, UpdateUserDto dto) {
        User user = users.stream().filter(u -> u.getId() == id).findFirst().orElse(null);
        if (user == null) return new Object() { public String error = "User not found"; };

        user.setName(dto.name);
        user.setEmail(dto.email);

        return UserMapper.toResponse(user);
    }

    @Override
    public Object partialUpdate(int id, PartialUpdateUserDto dto) {
        User user = users.stream().filter(u -> u.getId() == id).findFirst().orElse(null);
        if (user == null) return new Object() { public String error = "User not found"; };

        if (dto.name != null) user.setName(dto.name);
        if (dto.email != null) user.setEmail(dto.email);

        return UserMapper.toResponse(user);
    }

    @Override
    public Object delete(int id) {
        boolean removed = users.removeIf(u -> u.getId() == id);
        if (!removed) return new Object() { public String error = "User not found"; };

        return new Object() { public String message = "Deleted successfully"; };
    }
}
