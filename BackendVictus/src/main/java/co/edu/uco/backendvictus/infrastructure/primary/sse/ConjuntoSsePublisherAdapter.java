package co.edu.uco.backendvictus.infrastructure.primary.sse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import co.edu.uco.backendvictus.application.port.ConjuntoEventoPublisher;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Flux;

// Clase legacy no usada como bean; mantenida para referencia temporal
public class ConjuntoSsePublisherAdapter implements ConjuntoEventoPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConjuntoSsePublisherAdapter.class);
    private final Sinks.Many<Evento> sink = Sinks.many().multicast().onBackpressureBuffer();

    @Override
    public Mono<Void> publish(final Evento evento) {
        Sinks.EmitResult result = sink.tryEmitNext(evento);
        if (result.isFailure()) {
            LOGGER.warn("Fallo emitiendo evento SSE de conjunto (legacy): {} - resultado: {}", evento, result);
        }
        return Mono.empty();
    }

    @Override
    public Flux<Evento> stream() { return sink.asFlux(); }
}
