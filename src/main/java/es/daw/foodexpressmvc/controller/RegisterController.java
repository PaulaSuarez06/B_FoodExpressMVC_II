package es.daw.foodexpressmvc.controller;

import es.daw.foodexpressmvc.dto.UserRegisterDTO;
import es.daw.foodexpressmvc.exception.PasswordsDoNotMatchException;
import es.daw.foodexpressmvc.exception.UsernameAlreadyExistsException;
import es.daw.foodexpressmvc.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/register")
public class RegisterController {

    private final UserService userService;

    @GetMapping
    public String showRegisterForm(Model model) {
        model.addAttribute("userRegisterDTO", new UserRegisterDTO());
        return "register";
    }

    @PostMapping
    //PONGO VALID PORQUE EL DTO TIENE VALIDACIONES
    //LE PASO AL MODEL EL DTO PARA QUE LO MUESTR EN EL FORMARIO CON .
    public String register(
            @Valid @ModelAttribute("userRegisterDTO") UserRegisterDTO dto,
            BindingResult bindingResult,
            Model model
    ){
        if (bindingResult.hasErrors()) {
            return "register";
        }

        try{
            // Registrar usuario
            userService.register(dto);

            // Redirige a login indicando que se ha registrado correctamente
            return "redirect:/login?registered";

        }catch (UsernameAlreadyExistsException e){
            bindingResult.rejectValue("username", "username.exists",e.getMessage());
            model.addAttribute("errorMessage",e.getMessage());
            return "register";
        }catch(PasswordsDoNotMatchException e){
            bindingResult.rejectValue("confirmPassword", "password.mismatch",e.getMessage());
            //bindingResult.rejectValue("confirmPassword", "password.mismatch"); //En observación!!!
            model.addAttribute("errorMessage",e.getMessage());
            return "register";
        }

    }



}
