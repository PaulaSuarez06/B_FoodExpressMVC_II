package es.daw.foodexpressmvc.service;

import es.daw.foodexpressmvc.dto.UserRegisterDTO;
import es.daw.foodexpressmvc.entity.Role;
import es.daw.foodexpressmvc.entity.User;
import es.daw.foodexpressmvc.exception.PasswordsDoNotMatchException;
import es.daw.foodexpressmvc.exception.UsernameAlreadyExistsException;
import es.daw.foodexpressmvc.repository.RoleRepository;
import es.daw.foodexpressmvc.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    // PENDIENTE!!! leer de un fichero de propiedades propio foodExpress.properties
    /*
        api.api-url=http://localhost:8084/api
        api.auth-url=http://localhost:8084/auth/login

        api.auth-username=admin
        api.auth-password=melola
     */
    private static final String ROLE_DEFAULT = "ROLE_USER";

    @Override
    public void register(UserRegisterDTO dto) {

        // Validación de username único
        if(userRepository.findByUsername(dto.getUsername()).isPresent()){
            throw new UsernameAlreadyExistsException("Username already exists");
        }

        // Validar contraseñas
        if(!dto.getPassword().equals(dto.getConfirmPassword())){
            throw new PasswordsDoNotMatchException("Passwords do not match");
        }

        // Obtener el rol USER
        Role role = roleRepository.findByName(ROLE_DEFAULT).get(); // damos por hecho que existe!!!!

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.addRole(role);

        userRepository.save(user);

    }

    public Long getUserIdFromPrincipal(Principal principal) {

        if (principal == null) {
            throw new IllegalStateException("No authenticated user found");
        }

        String username = principal.getName(); // viene de Spring Security

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new IllegalStateException("User not found for username: " + username)
                );

        return user.getId();
    }





}
