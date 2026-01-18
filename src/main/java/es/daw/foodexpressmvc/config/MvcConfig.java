package es.daw.foodexpressmvc.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("home");
        registry.addViewController("/home").setViewName("home");
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/public").setViewName("public-info");
        registry.addViewController("/error").setViewName("error");
        registry.addViewController("/dashboard").setViewName("dashboard");
        registry.addViewController("/restaurants/menu").setViewName("restaurants/restaurants-menu");
        registry.addViewController("/restaurants/create-success").setViewName("restaurants/create-success");
        registry.addViewController("/orders/order-create").setViewName("orders/order-create");
    }
}
