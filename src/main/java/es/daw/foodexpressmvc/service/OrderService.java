package es.daw.foodexpressmvc.service;

import es.daw.foodexpressmvc.dto.*;
import es.daw.foodexpressmvc.dto.order.CreateOrderDTO;
import es.daw.foodexpressmvc.dto.order.OrderCreatedResponseDTO;
import es.daw.foodexpressmvc.dto.order.OrderResponseDTO;
import es.daw.foodexpressmvc.exception.ConnectionApiRestException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
@Service
public class OrderService {

    private final WebClient webClientAPI;
    private final ApiAuthService apiAuthService;

    public PageResponse<OrderResponseDTO> filterOrders(String status, Long userId, Long restaurantId, int page, int size, String sort, String dir) {



//        try {
            return webClientAPI
                    .get()
                    .uri(uriBuilder -> {
                        uriBuilder.path("/orders")
                                .queryParam("page",page)
                                .queryParam("size", size)
                                .queryParam("sort", sort + "," + dir);
                        //DIR ES UN VALIR CONCATENADO AL SORT




                        if (status != null && !status.isBlank()) {
                            uriBuilder.queryParam("status", status);
                        }
                        if (userId != null) {
                            uriBuilder.queryParam("userId", userId);
                        }
                        if (restaurantId != null) {
                            uriBuilder.queryParam("restaurantId", restaurantId);
                        }


                        return uriBuilder.build();
                    })
                    .retrieve()
                    .onStatus(
                            httpStatus -> httpStatus.is4xxClientError() || httpStatus.is5xxServerError(),
                            clientResponse -> clientResponse
                                    .bodyToMono(ErrorDTO.class)
                                    .defaultIfEmpty(new ErrorDTO())
                                    .flatMap(errorDto -> Mono.error(
                                            new ConnectionApiRestException(
                                                    buildApiErrorMessage(errorDto.getPath(), errorDto.getStatus(), errorDto)
                                            )
                                    ))
                    )

                    .bodyToMono(new ParameterizedTypeReference<PageResponse<OrderResponseDTO>>() {})
                    .onErrorMap(ex -> {
                        if (ex instanceof ConnectionApiRestException) return ex;
                        return new ConnectionApiRestException("No se pudo conectar con el API (GET /dishes). Detalle: " + ex.getMessage());
                    })

                    .block();



    }


    //  en este caso la api devuelve mas lineas, mando createorder con lo minimo para crar,
    // y me devuelve la respuesta con mas lineas
    public OrderCreatedResponseDTO createOrder(CreateOrderDTO dto) {

        String token = apiAuthService.getToken();

        return webClientAPI
                .post()
                .uri("/orders")
                .header("Authorization", "Bearer " + token)
                .bodyValue(dto)
                .retrieve()
                .onStatus(
                        httpStatus -> httpStatus.is4xxClientError() || httpStatus.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(ErrorDTO.class)
                                .defaultIfEmpty(new ErrorDTO())
                                .flatMap(errorDto -> Mono.error(new ConnectionApiRestException(
                                        buildApiErrorMessage(errorDto.getPath(), errorDto.getStatus(), errorDto)
                                )))
                )
                .bodyToMono(OrderCreatedResponseDTO.class)
                .onErrorMap(ex -> {
                    if (ex instanceof ConnectionApiRestException) return ex;
                    return new ConnectionApiRestException("No se pudo conectar con el API (POST /orders). Detalle: " + ex.getMessage());
                })
                .block();
    }




    private String buildApiErrorMessage(String operation, int httpStatus, ErrorDTO errorDto) {

        String apiMessage = (errorDto.getMessage() != null && !errorDto.getMessage().isBlank())
                ? errorDto.getMessage()
                : "sin detalle";

        String apiError = (errorDto.getError() != null && !errorDto.getError().isBlank())
                ? errorDto.getError()
                : "HTTP error";

        String apiPath = (errorDto.getPath() != null && !errorDto.getPath().isBlank())
                ? errorDto.getPath()
                : "(path no informado)";

        return """
           %s falló.
           HTTP %d - %s
           Message: %s
           Path: %s
           """.formatted(operation, httpStatus, apiError, apiMessage, apiPath).trim();
    }




}
