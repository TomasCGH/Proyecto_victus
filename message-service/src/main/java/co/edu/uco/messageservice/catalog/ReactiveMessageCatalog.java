package co.edu.uco.messageservice.catalog;

import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

/**
 * Catálogo reactivo que delega en el catálogo estático {@link MessageCatalog}
 * como única fuente de verdad, manteniendo un canal de eventos para notificar
 * cambios.
 */
@Component
public class ReactiveMessageCatalog {

    private final Sinks.Many<MessageChange> sink = Sinks.many().replay().latest();
    private final Flux<MessageChange> changeStream = sink.asFlux();

    public ReactiveMessageCatalog() {
        // Sin carga de defaults aquí; los valores provienen de MessageCatalog
    }

    public Flux<Message> findAll() {
        return Flux.defer(() -> Flux.fromIterable(MessageCatalog.getAllMessages().values())
                .map(this::copyOf));
    }

    public Mono<Message> findByKey(String key) {
        return Mono.defer(() -> Mono.justOrEmpty(MessageCatalog.getMessageValue(key))
                .map(this::copyOf));
    }

    public Mono<Message> save(Message message) {
        return Mono.fromSupplier(() -> {
            Message sanitized = copyOf(message);
            CatalogEventType type = MessageCatalog.getMessageValue(sanitized.getKey()) != null
                    ? CatalogEventType.UPDATED
                    : CatalogEventType.CREATED;
            MessageCatalog.synchronizeMessageValue(sanitized);
            emit(type, sanitized);
            return copyOf(sanitized);
        });
    }

    public Mono<Message> remove(String key) {
        return Mono.defer(() -> {
            Message removed = MessageCatalog.removeMessage(key);
            if (removed == null) {
                return Mono.empty();
            }
            emit(CatalogEventType.DELETED, removed);
            return Mono.just(copyOf(removed));
        });
    }

    public Flux<MessageChange> changes() {
        return changeStream;
    }

    private void emit(CatalogEventType type, Message message) {
        sink.emitNext(new MessageChange(type, copyOf(message)), Sinks.EmitFailureHandler.FAIL_FAST);
    }

    private Message copyOf(Message message) {
        return new Message(message.getKey(), message.getValue());
    }
}
