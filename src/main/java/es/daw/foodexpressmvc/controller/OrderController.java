package es.daw.foodexpressmvc.controller;

import es.daw.foodexpressmvc.dto.*;
import es.daw.foodexpressmvc.dto.order.*;
import es.daw.foodexpressmvc.service.DishService;
import es.daw.foodexpressmvc.service.OrderService;
import es.daw.foodexpressmvc.service.RestaurantsService;
import es.daw.foodexpressmvc.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;


@RequiredArgsConstructor
@RequestMapping("/orders")
@Controller

// CREAR UN PEDIDO
public class OrderController {

//dos metodos en el mismo controller

    private final OrderService orderService;
    private final UserServiceImpl userService;
    private final RestaurantsService  restaurantService;
    private final DishService dishService;

//este medoto muestra el filter vacio, muestra una página
    @GetMapping
    public String showOrderForm(Model model){
        model.addAttribute("filter", new OrderFilter());
        return "orders/orders-list";
    }


    @GetMapping("/search")
    public String searchOrders(@RequestParam(required = false) String status,
                               @RequestParam(required = false) Long userId,
                                @RequestParam(required = false) Long restaurantId,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "5") int size,
                               @RequestParam(defaultValue = "restaurant.name") String sort,
                               @RequestParam(defaultValue = "asc") String dir,
                               Model model){
    //1- que le formulario conserve lo escrito

        model.addAttribute("filter", new OrderFilter(status,userId,restaurantId));

    //2- buscar de verdad

        PageResponse<OrderResponseDTO> orders = orderService.filterOrders(status,userId,restaurantId, page, size, sort, dir);
        model.addAttribute("orders",orders.getContent());
        model.addAttribute("page",orders);
        model.addAttribute("size",size);
        model.addAttribute("sort",sort);
        model.addAttribute("dir",dir);
        return "orders/orders-list";




}


    @GetMapping("/new")
    public String showCreateForm(@RequestParam(required = false) Long restaurantId,
                                 Model model) {

        // 1. Formulario
        CreateOrderForm form = new CreateOrderForm();
        form.setRestaurantId(restaurantId);
        model.addAttribute("orderForm", form);

        // 2. Restaurantes
        model.addAttribute("restaurants", restaurantService.getAllRestaurants());

        // 3. Platos del restaurante (si hay uno seleccionado)
        if (restaurantId != null) {
            model.addAttribute("dishes", dishService.findById(restaurantId));
        } else {
            model.addAttribute("dishes", List.of());
        }

        return "orders/order-create";
    }



    //DESPUES DE POST SIEMPRE REDIRECT
    @PostMapping
    public String createOrder(@ModelAttribute("orderForm") CreateOrderForm form,
                              Principal principal) {

        Long userId = userService.getUserIdFromPrincipal(principal);

        CreateOrderDTO dto = new CreateOrderDTO(
                userId,
                form.getRestaurantId(),
                form.getItems().stream()
                        .filter(i -> i.getDishId() != null && i.getQuantity() != null)
                        .map(i -> new OrderItemDTO(i.getDishId(), i.getQuantity()))
                        .toList()
        );

        orderService.createOrder(dto);
        return "redirect:/orders";
    }





}
