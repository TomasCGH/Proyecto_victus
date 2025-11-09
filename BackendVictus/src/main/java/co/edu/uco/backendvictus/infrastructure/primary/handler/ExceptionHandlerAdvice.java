package co.edu.uco.backendvictus.infrastructure.primary.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;

import co.edu.uco.backendvictus.crosscutting.exception.ApplicationException;
import co.edu.uco.backendvictus.crosscutting.exception.DomainException;
import co.edu.uco.backendvictus.crosscutting.exception.InfrastructureException;
import co.edu.uco.backendvictus.crosscutting.helpers.LoggerHelper;
import co.edu.uco.backendvictus.infrastructure.primary.response.ApiErrorResponse;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

    private static final org.slf4j.Logger LOGGER = LoggerHelper.getLogger(ExceptionHandlerAdvice.class);

    @ExceptionHandler(DomainException.class)
    public Mono<ResponseEntity<ApiErrorResponse>> handleDomainException(final DomainException exception,
            final ServerWebExchange exchange) {
        LOGGER.warn("Error de dominio: {}", exception.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "DOMAIN_ERROR", exception, exchange);
    }

    @ExceptionHandler(ApplicationException.class)
    public Mono<ResponseEntity<ApiErrorResponse>> handleApplicationException(final ApplicationException exception,
            final ServerWebExchange exchange) {
        LOGGER.warn("Error de aplicacion: {}", exception.getMessage());
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, "APPLICATION_ERROR", exception, exchange);
    }

    @ExceptionHandler(InfrastructureException.class)
    public Mono<ResponseEntity<ApiErrorResponse>> handleInfrastructureException(
            final InfrastructureException exception, final ServerWebExchange exchange) {
        LOGGER.error("Error de infraestructura", exception);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "INFRASTRUCTURE_ERROR", exception, exchange);
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ApiErrorResponse>> handleGenericException(final Exception exception,
            final ServerWebExchange exchange) {
        LOGGER.error("Error inesperado", exception);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", exception, exchange);
    }

    private Mono<ResponseEntity<ApiErrorResponse>> buildResponse(final HttpStatus status, final String code,
            final Exception exception, final ServerWebExchange exchange) {
        final String path = exchange.getRequest().getPath().value();
        final ApiErrorResponse response = ApiErrorResponse.of(code, exception.getMessage(), path);
        return Mono.just(ResponseEntity.status(status).body(response));
    }
}
