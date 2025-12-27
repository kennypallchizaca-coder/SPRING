package ec.edu.ups.icc.fundamentos01.users.mappers;

import ec.edu.ups.icc.fundamentos01.users.dtos.CreateUserDto;
import ec.edu.ups.icc.fundamentos01.users.dtos.UserResponseDto;
import ec.edu.ups.icc.fundamentos01.users.models.User;

public class UserMapper {

    /**
     * Crea un User desde CreateUserDto
     */
    public static User fromCreateDto(CreateUserDto dto) {
        return User.fromDto(dto);
    }

    /**
     * Convierte User a UserResponseDto
     */
    public static UserResponseDto toResponse(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.id = user.getId();
        dto.name = user.getName();
        dto.email = user.getEmail();
        return dto;
    }
}
