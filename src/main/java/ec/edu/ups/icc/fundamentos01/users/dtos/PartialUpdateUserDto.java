package ec.edu.ups.icc.fundamentos01.users.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class PartialUpdateUserDto {

    @Size(min = 3, max = 150, message = "El nombre debe tener entre 3 y 150 caracteres")
    public String name;

    @Email(message = "Debe ingresar un email válido")
    @Size(max = 150)
    public String email;

    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    public String password;
}
