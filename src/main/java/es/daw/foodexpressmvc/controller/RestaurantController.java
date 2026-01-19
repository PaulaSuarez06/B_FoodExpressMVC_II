package es.daw.foodexpressmvc.controller;

import es.daw.foodexpressmvc.dto.RestaurantDTO;
import es.daw.foodexpressmvc.service.RestaurantsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/restaurants")
public class RestaurantController {

    private final RestaurantsService  restaurantsService;

    @GetMapping
    public String listRestaurants(Model model) {
        List<RestaurantDTO> restaurants = restaurantsService.getAllRestaurants();
        model.addAttribute("restaurants", restaurants);
        return "restaurants/restaurants";
    }


    @GetMapping("/create")
    public String showForm(Model model, Principal principal) {
        model.addAttribute(principal.getName());
        model.addAttribute("restaurant", new RestaurantDTO());
        model.addAttribute("mode","create");
        return "restaurants/restaurant-form";

        //INICIALIZAMOS CON UN DTO VACIO
        // LE PASAMOS EL MODE CREATE PARA QUE LO MUESTRE EN EL FORMULARIO Y TENGS CONDICIONES SEA PARA CREAR O EDITAR.
//            <h2 class="mb-4" th:text="${mode == 'create'}? 'Create Restaurant': 'Edit Restaurant'"></h2>
    }
//    RedirectAttributes PARA EVITAR DUPLICADOS
    @PostMapping("/create")
        //<form th:action="${mode == 'create'}? '/restaurants/create':'/restaurants/update/'+${restaurant.id}"
    public String create(@ModelAttribute("restaurant") RestaurantDTO restaurantDTO,
                         Model model,
                         RedirectAttributes redirectAttributes) {

        RestaurantDTO saved = restaurantsService.create(restaurantDTO);
//        model.addAttribute(saved);
//        return "restaurants/create-success";

        redirectAttributes.addFlashAttribute("restaurant", saved);
        //el saved es porque manda el objeto  guardado
        redirectAttributes.addFlashAttribute("success",true);
        //mete una variable de exito-- si hay seccess lo coge la vista

        return "redirect:create-success"; //este va unido a restaurant


    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        restaurantsService.delete(id);
        return "redirect:/restaurants";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {

        RestaurantDTO restDTO = restaurantsService.findById(id);

        model.addAttribute("mode","update");
        model.addAttribute("restaurant",restDTO);
        return "restaurants/restaurant-form";

    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("restaurant") RestaurantDTO restaurantDTO,
                         BindingResult bindingResult,
                         Model model
    ){

        if (bindingResult.hasErrors()){
            model.addAttribute("mode","update");
            return "restaurants/restaurant-form";
        }

        restaurantsService.update(id,restaurantDTO);
        return "redirect:/restaurants";
    }

}
