package co.edu.uco.messageservice.controller;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.uco.messageservice.catalog.CatalogEventType;
import co.edu.uco.messageservice.catalog.Message;
import co.edu.uco.messageservice.service.ReactiveMessageService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Controlador REST responsable de exponer el catálogo de mensajes asociados al
 * dominio de conjuntos residenciales. Mantiene la semántica reactiva requerida
 * por Spring WebFlux y aplica encabezados de no cacheo en cada respuesta.
 */
@RestController
@RequestMapping("/api/v1/messages")
public class MessageController {

    private static final CacheControl NO_CACHE = CacheControl.noStore().mustRevalidate();

    private final ReactiveMessageService service;

    public MessageController(ReactiveMessageService service) {
        this.service = service;
    }

    /**
     * Obtiene de forma reactiva todos los mensajes disponibles.
     */
    @GetMapping
    public Flux<Message> getAllMessages() {
        return service.findAll();
    }

    /**
     * Busca un mensaje específico por su clave única.
     */
    @GetMapping("/{key}")
    public Mono<ResponseEntity<Message>> getMessage(@PathVariable String key) {
        return service.findByKey(key)
                .map(value -> ResponseEntity.ok()
                        .cacheControl(NO_CACHE)
                        .header("Pragma", "no-cache")
                        .header("Expires", "0")
                        .body(value))
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .cacheControl(NO_CACHE)
                        .header("Pragma", "no-cache")
                        .header("Expires", "0")
                        .build()));
    }

    /**
     * Crea o actualiza un mensaje del catálogo.
     */
    @PostMapping
    public Mono<ResponseEntity<Message>> createMessage(@RequestBody Message body) {
        Message sanitizedMessage = new Message(body.getKey(), body.getValue());
        return service.upsert(sanitizedMessage)
                .map(saved -> ResponseEntity.status(HttpStatus.CREATED)
                        .cacheControl(NO_CACHE)
                        .header("Pragma", "no-cache")
                        .header("Expires", "0")
                        .body(saved));
    }

    /**
     * Actualiza el valor de un mensaje previamente registrado.
     */
    @PutMapping("/{key}")
    public Mono<ResponseEntity<Message>> updateMessage(@PathVariable String key, @RequestBody Message body) {
        Message sanitizedMessage = new Message(key, body.getValue());
        return service.upsert(sanitizedMessage)
                .map(saved -> ResponseEntity.ok()
                        .cacheControl(NO_CACHE)
                        .header("Pragma", "no-cache")
                        .header("Expires", "0")
                        .body(saved));
    }

    /**
     * Expone un flujo continuo de los mensajes modificados.
     */
    @GetMapping(value = "/stream", produces = "text/event-stream")
    public Flux<ServerSentEvent<Message>> streamUpdates() {
        return service.listenChanges()
                .filter(change -> change.type() != CatalogEventType.DELETED)
                .map(change -> ServerSentEvent.<Message>builder(change.payload())
                        .event(change.type().name())
                        .build());
    }
}
