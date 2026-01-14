package es.daw.foodexpressmvc.controller;

import es.daw.foodexpressmvc.dto.DishResponseDTO;
import es.daw.foodexpressmvc.dto.PageResponse;
import es.daw.foodexpressmvc.service.DishService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class DishController {

    private final DishService dishService;

//    @GetMapping("/dishes")
//    public String listDishes(Model model) {
//
//        // Necesito pasar el número de página y el tamaño
//        model.addAttribute("dishes", dishService.getAllDishes());
//
//        return "dishes/dishes";
//    }


    @GetMapping("/dishes")
    public String listDishes(@RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "5") int size,
                             @RequestParam(defaultValue = "name") String sort,
                             @RequestParam(defaultValue = "asc") String dir,
                             Model model) {

        // Pendiente!!!! la lista permite seleccionar más de un campo para ordenar...
        // Cómo lo recibo en el endpoint?

        // Qué estructura de datos uso? un array, una lista...
        // Si todos los campos se ordenar por el mismo criterio(dir) o no???
        // Y si cada campo viene del cliente con su dir??
        // MAP o bean ...

        PageResponse<DishResponseDTO> dishPage = dishService.getAllDishes(page, size,sort,dir);

        model.addAttribute("page", dishPage);

        // Parar que la lista de campos por los que ordenar dependa del controlador!!!
        //model.addAttribute("sortOptions", List.of("name","category","price"));

//        model.addAttribute("dishes", dishPage.getContent());
//
//        model.addAttribute("sort", sort);
//        model.addAttribute("dir", dir);
//        model.addAttribute("size", size);

        // Pendiente mandar a vista solo dishPage!!!! (Alejandro)


        return "dishes/dishes";
    }
}
