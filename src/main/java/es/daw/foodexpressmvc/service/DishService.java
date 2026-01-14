package es.daw.foodexpressmvc.service;

import es.daw.foodexpressmvc.dto.DishDTO;
import es.daw.foodexpressmvc.dto.DishResponseDTO;
import es.daw.foodexpressmvc.dto.ErrorDTO;
import es.daw.foodexpressmvc.dto.PageResponse;
import es.daw.foodexpressmvc.exception.ConnectionApiRestException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;


//@Service
//@RequiredArgsConstructor
//public class DishService {
//
//    private final WebClient webClientAPI;
//
//    public List<DishResponseDTO> getAllDishes() {
//
//        DishResponseDTO[] dishes;
//        try{
//            dishes = webClientAPI.get()
//                    .uri("/dishes")
//                    .retrieve()
//                    .bodyToMono(DishResponseDTO[].class)
//                    .block();
//
//            return Arrays.asList(dishes);
//        }catch (Exception e){
//            throw new ConnectionApiRestException(e.getMessage());
//        }
//
//
//    }
//}

@Service
@RequiredArgsConstructor
public class DishService {

    private final WebClient webClientAPI;

    // Puedo recibir un map con los sorts y dirs correspondientes...
    // Montario en la uri tantos sort con su dir como campos haya seleccionado...
    public PageResponse<DishResponseDTO> getAllDishes(int page, int size, String sort, String dir ){

        //try {
            return webClientAPI
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/dishes")
                            .queryParam("page", page)
                            .queryParam("size", size)
                            .queryParam("sort", sort + "," + dir)
                            //.queryParam("sort", sort + "," + dir) // otro campo a ordenar
                            .build()
                    )
                    .retrieve()
                    // Si el status es 4xx o 5xx, intento leer un ErrorDTO
                    /*
                    onStatus(
                    Predicate<HttpStatusCode> statusPredicate,
                    Function<ClientResponse, Mono<? extends Throwable>> exceptionFunction)
                     */

                    .onStatus(
                            httpStatus -> httpStatus.is4xxClientError() || httpStatus.is5xxServerError(),
                            clientResponse -> clientResponse.bodyToMono(ErrorDTO.class)
                                    .defaultIfEmpty(new ErrorDTO()) // por si el body viene vacío
//                                    .flatMap(errorDto -> {
//                                        String msg = "Error al llamar al API /dishes: "
//                                                + (errorDto.getMessage() != null ? errorDto.getMessage() : "sin detalle");
//                                        return Mono.error(new ConnectionApiRestException(msg));
//                                    })
                                    .flatMap(errorDto -> Mono.error(new ConnectionApiRestException(
                                            buildApiErrorMessage(errorDto.getPath(), errorDto.getStatus(), errorDto)
                                    )))
                    )
                    .bodyToMono(new ParameterizedTypeReference<PageResponse<DishResponseDTO>>() {})
                    // Si hay fallo de red/timeout, lo envolvemos con un mensaje consistente
                    .onErrorMap(ex -> {
                        if (ex instanceof ConnectionApiRestException) return ex;
                        return new ConnectionApiRestException("No se pudo conectar con el API (GET /dishes). Detalle: " + ex.getMessage());
                    })
                    .block(); // En MVC clásico, bloqueamos aquí
//        } catch (Exception e) {
//            // Aquí puedes loguear o wrappear más info
//            throw new ConnectionApiRestException(e.getMessage());
//        }
    }

    /**
     * Construye un mensaje de error detallado a partir de la información del ErrorDTO recibido desde el API.
     *
     *
     * PENDIENTE DARLE UNA VUELTA!!!!
     * @param operation
     * @param httpStatus
     * @param errorDto
     * @return
     */
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
