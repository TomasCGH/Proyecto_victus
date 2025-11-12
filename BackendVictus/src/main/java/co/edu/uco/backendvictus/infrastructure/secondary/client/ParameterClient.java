package co.edu.uco.backendvictus.infrastructure.secondary.client;

import co.edu.uco.backendvictus.crosscutting.helpers.LoggerHelper;
import org.slf4j.Logger;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class ParameterClient {

    private static final Logger LOGGER = LoggerHelper.getLogger(ParameterClient.class);

    private final WebClient webClient;

    public record ParameterResult(String key, String value, String source) {}

    private record RemoteParameterResponse(String key, String value) {}

    public ParameterClient(final WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<ParameterResult> get(final String key) {
        LOGGER.info("ParameterClient → consultando parámetro '{}'", key);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/{key}").build(key))
                .retrieve()
                .bodyToMono(RemoteParameterResponse.class)
                .timeout(Duration.ofSeconds(3))
                .map(resp -> {
                    final String value = resp != null ? resp.value() : null;
                    LOGGER.info("ParameterService → parámetro \"{}\" = {}", key, value);
                    return new ParameterResult(key, value, "parameter-service");
                })
                .onErrorResume(ex -> {
                    LOGGER.warn("ParameterClient → no disponible, usando fallback para '{}'", key, ex);
                    return Mono.just(new ParameterResult(key, null, "backend-fallback"));
                });
    }

    public static ParameterClient fallback() {
        return new ParameterClient(WebClient.builder().baseUrl("http://localhost").build()) {
            @Override
            public Mono<ParameterResult> get(final String key) {
                return Mono.just(new ParameterResult(key, null, "backend-fallback"));
            }
        };
    }
}
