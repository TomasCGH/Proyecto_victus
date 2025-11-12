package co.edu.uco.backendvictus.infrastructure.secondary.client;

import co.edu.uco.backendvictus.crosscutting.helpers.LoggerHelper;
import org.slf4j.Logger;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class MessageClient {

    private static final Logger LOGGER = LoggerHelper.getLogger(MessageClient.class);

    private final WebClient webClient;

    public record MessageResult(String message, String source) {}

    // Estructura flexible para respuestas del servicio de mensajes
    private record RemoteMessageResponse(Boolean success, String code, String message) {}

    public MessageClient(final WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<MessageResult> getMessage(final String codeKey) {
        LOGGER.info("MessageClient → consultando mensaje con codeKey='{}'", codeKey);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/{code}").build(codeKey))
                .retrieve()
                .bodyToMono(RemoteMessageResponse.class)
                .timeout(Duration.ofSeconds(3))
                .map(resp -> {
                    final String msg = resp != null && resp.message() != null ? resp.message() : fallbackMessage();
                    final String source = (resp != null && resp.message() != null) ? "message-service" : "backend-fallback";
                    LOGGER.info("MessageClient → respuesta desde {}: '{}'", source, msg);
                    return new MessageResult(msg, source);
                })
                .onErrorResume(throwable -> {
                    final String fallback = fallbackMessage();
                    if (throwable instanceof WebClientResponseException wcre) {
                        LOGGER.error("MessageClient → error {} desde message-service, usando fallback: {}", wcre.getRawStatusCode(), fallback);
                    } else {
                        LOGGER.error("MessageClient → error al consultar message-service, usando fallback: {}", fallback, throwable);
                    }
                    return Mono.just(new MessageResult(fallback, "backend-fallback"));
                });
    }

    private String fallbackMessage() {
        return "Error genérico. El servicio de mensajes no está disponible.";
    }

    public static MessageClient fallback() {
        return new MessageClient(WebClient.builder().baseUrl("http://localhost").build()) {
            @Override
            public Mono<MessageResult> getMessage(final String codeKey) {
                return Mono.just(new MessageResult("Error genérico. El servicio de mensajes no está disponible.", "backend-fallback"));
            }
        };
    }
}
