package es.daw.foodexpressmvc.controller;

import es.daw.foodexpressmvc.dto.DishResponseDTO;
import es.daw.foodexpressmvc.dto.PageResponse;
import es.daw.foodexpressmvc.service.DishService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

        PageResponse<DishResponseDTO> dishPage = dishService.getAllDishes(page, size,sort,dir);

        model.addAttribute("page", dishPage);

        model.addAttribute("dishes", dishPage.getContent());

        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);
        model.addAttribute("size", size);



        return "dishes/dishes";
    }
}
