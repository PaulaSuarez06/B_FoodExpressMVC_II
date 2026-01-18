package es.daw.foodexpressmvc.dto;

import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegisterDTO {

    @NotBlank(message = "{user.username.notblank}")
    @Size(min = 4, message = "{user.username.size}")
    private String username;

    @Size(max = 100, message = "{user.fullname.size}")
    private String fullName;

    @NotBlank(message = "{user.email.notblank}")
    private String email;

    @NotBlank(message = "{user.password.notblank}")
    private String password;

    @NotBlank(message = "{user.confirmPassword.notblank}")
    private String confirmPassword;
}

