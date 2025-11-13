package co.edu.uco.backendvictus.infrastructure.secondary.client;

import co.edu.uco.backendvictus.crosscutting.helpers.LoggerHelper;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.net.ConnectException;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

public class MessageClient {

    private static final Logger LOGGER = LoggerHelper.getLogger(MessageClient.class);

    private final WebClient webClient;

    public record MessageResult(String technicalMessage, String clientMessage, String source) {}

    // Estructura flexible: soporta payloads {key,value} ó {key,technicalMessage,clientMessage}
    private record RemoteMessageResponse(String key, String value, String technicalMessage, String clientMessage) {}

    public MessageClient(final WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<MessageResult> getMessage(final String key) {
        LOGGER.info("MessageClient → consultando mensaje con key='{}'", key);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/{key}").build(key))
                .retrieve()
                .bodyToMono(RemoteMessageResponse.class)
                .timeout(Duration.ofSeconds(3))
                .map(resp -> mapToResultOrDefault(key, resp))
                .onErrorResume(throwable -> handleErrors(key, throwable));
    }

    private MessageResult mapToResultOrDefault(final String key, final RemoteMessageResponse resp) {
        if (resp == null) {
            return serviceDownFallback();
        }
        final String technical = nonBlank(resp.technicalMessage()) ? resp.technicalMessage()
                : (nonBlank(resp.value()) ? resp.value() : missingKeyTechnical(key));
        final String client = nonBlank(resp.clientMessage()) ? resp.clientMessage()
                : (nonBlank(resp.value()) ? resp.value() : missingKeyClient());
        final String source = (nonBlank(resp.technicalMessage()) || nonBlank(resp.clientMessage()) || nonBlank(resp.value()))
                ? "message-service" : "backend-fallback";
        LOGGER.info("MessageClient → respuesta desde {} (key='{}')", source, key);
        return new MessageResult(technical, client, source);
    }

    private Mono<MessageResult> handleErrors(final String key, final Throwable throwable) {
        if (throwable instanceof WebClientResponseException wcre) {
            final HttpStatus status = (HttpStatus) wcre.getStatusCode();
            if (status == HttpStatus.NOT_FOUND) {
                // Key inexistente → defaults de missing key
                LOGGER.warn("MessageClient → key '{}' no encontrada (404). Usando defaults.", key);
                return Mono.just(new MessageResult(missingKeyTechnical(key), missingKeyClient(), "backend-fallback"));
            }
            if (status.is5xxServerError()) {
                // Servicio arriba pero con error → fallback de servicio caído
                LOGGER.error("MessageClient → error {} en message-service. Usando fallback.", status.value());
                return Mono.just(serviceDownFallback());
            }
            // Otros 4xx → propaga (p.e. 400)
            LOGGER.error("MessageClient → error {} en message-service, propagando.", status.value());
            return Mono.error(wcre);
        }
        if (throwable instanceof WebClientRequestException || throwable instanceof ConnectException || throwable instanceof TimeoutException) {
            LOGGER.error("MessageClient → conectividad/timeout con message-service. Usando fallback.", throwable);
            return Mono.just(serviceDownFallback());
        }
        LOGGER.error("MessageClient → error inesperado. Usando fallback.", throwable);
        return Mono.just(serviceDownFallback());
    }

    private static boolean nonBlank(final String s) {
        return s != null && !s.isBlank();
    }

    private MessageResult serviceDownFallback() {
        return new MessageResult(
                "Technical error: message-service unavailable.",
                "Error genérico. El servicio de mensajes no está disponible.",
                "backend-fallback");
    }

    private String missingKeyTechnical(final String key) {
        return "Technical error: missing message key " + key + ".";
    }

    private String missingKeyClient() {
        return "Ocurrió un error al procesar tu solicitud, por favor inténtalo de nuevo.";
    }

    public static MessageClient fallback() {
        return new MessageClient(WebClient.builder().baseUrl("http://localhost").build()) {
            @Override
            public Mono<MessageResult> getMessage(final String key) {
                return Mono.just(new MessageResult(
                        "Technical error: message-service unavailable.",
                        "Error genérico. El servicio de mensajes no está disponible.",
                        "backend-fallback"));
            }
        };
    }
}
