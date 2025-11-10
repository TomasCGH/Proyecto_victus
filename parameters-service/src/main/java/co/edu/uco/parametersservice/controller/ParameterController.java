package co.edu.uco.parametersservice.controller;

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

import co.edu.uco.parametersservice.catalog.CatalogEventType;
import co.edu.uco.parametersservice.catalog.Parameter;
import co.edu.uco.parametersservice.service.ReactiveParameterService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Controlador que expone la configuración dinámica asociada a la gestión de
 * viviendas. Todas las respuestas aplican políticas de no cacheo para garantizar
 * la lectura de valores actualizados.
 */
@RestController
@RequestMapping("/api/v1/viviendas/parameters")
public class ParameterController {

    /**
     * Ejemplo de payload esperado:
     *
     * <pre>
     * {
     *   "id": "b6e231e2-ff33-44e8-8a9b-37eab1e0a701",
     *   "numero": "A-301",
     *   "tipo": "APARTAMENTO",
     *   "estado": "DISPONIBLE",
     *   "conjunto": {
     *     "id": "f2f1ab32-cc77-4a22-b93a-3c13b68a7d99",
     *     "nombre": "Conjunto Los Cedros"
     *   }
     * }
     * </pre>
     */
    private static final CacheControl NO_CACHE = CacheControl.noStore().mustRevalidate();
    private static final String RESERVA_EXPIRACION_KEY = "gestion.vivienda.reserva.expiracionHoras";
    private static final String INSPECCION_RECORDATORIOS_KEY = "gestion.vivienda.inspeccion.maxRecordatorios";

    private final ReactiveParameterService service;

    public ParameterController(ReactiveParameterService service) {
        this.service = service;
    }

    /**
     * Retorna el listado completo de parámetros relacionados con viviendas.
     */
    @GetMapping
    public Flux<Parameter> getAllViviendaParameters() {
        return service.findAll();
    }

    /**
     * Obtiene el valor de un parámetro particular.
     */
    @GetMapping("/{key}")
    public Mono<ResponseEntity<Parameter>> getViviendaParameter(@PathVariable String key) {
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
     * Permite registrar o actualizar parámetros personalizados.
     */
    @PostMapping
    public Mono<ResponseEntity<Parameter>> createViviendaParameter(@RequestBody Parameter body) {
        Parameter sanitizedParameter = new Parameter(body.getKey(), body.getValue());
        return service.upsert(sanitizedParameter)
                .map(saved -> ResponseEntity.status(HttpStatus.CREATED)
                        .cacheControl(NO_CACHE)
                        .header("Pragma", "no-cache")
                        .header("Expires", "0")
                        .body(saved));
    }

    /**
     * Actualiza el valor de un parámetro existente.
     */
    @PutMapping("/{key}")
    public Mono<ResponseEntity<Parameter>> updateViviendaParameter(@PathVariable String key, @RequestBody Parameter body) {
        Parameter sanitizedParameter = new Parameter(key, body.getValue());
        return service.upsert(sanitizedParameter)
                .map(saved -> ResponseEntity.ok()
                        .cacheControl(NO_CACHE)
                        .header("Pragma", "no-cache")
                        .header("Expires", "0")
                        .body(saved));
    }

    /**
     * Ajusta dinámicamente el tiempo máximo de expiración de una reserva de
     * vivienda.
     */
    @PutMapping("/reserva/expiracion")
    public Mono<ResponseEntity<Parameter>> updateReservaExpiracion(@RequestBody ReservaExpiracionRequest body) {
        int sanitizedHours = Math.max(body.horas(), 1);
        Parameter parameter = new Parameter(RESERVA_EXPIRACION_KEY, Integer.toString(sanitizedHours));
        return service.upsert(parameter)
                .map(saved -> ResponseEntity.ok()
                        .cacheControl(NO_CACHE)
                        .header("Pragma", "no-cache")
                        .header("Expires", "0")
                        .body(saved));
    }

    /**
     * Ajusta la cantidad máxima de recordatorios automáticos para inspecciones de
     * viviendas.
     */
    @PutMapping("/inspecciones/maximo-recordatorios")
    public Mono<ResponseEntity<Parameter>> updateInspeccionesMaximoRecordatorios(
            @RequestBody MaximoRecordatoriosRequest body) {
        int sanitizedAttempts = Math.max(body.recordatorios(), 1);
        Parameter parameter = new Parameter(INSPECCION_RECORDATORIOS_KEY, Integer.toString(sanitizedAttempts));
        return service.upsert(parameter)
                .map(saved -> ResponseEntity.ok()
                        .cacheControl(NO_CACHE)
                        .header("Pragma", "no-cache")
                        .header("Expires", "0")
                        .body(saved));
    }

    /*
     * @DeleteMapping("/{key}") public Mono<ResponseEntity<Void>>
     * deleteViviendaParameter(@PathVariable String key) { return service.delete(key)
     *         .map(removed -> ResponseEntity.noContent()
     *                 .cacheControl(NO_CACHE)
     *                 .header("Pragma", "no-cache")
     *                 .header("Expires", "0")
     *                 .build())
     *         .defaultIfEmpty(ResponseEntity.notFound().build()); }
     */

    /**
     * Expone un flujo continuo con los cambios en los parámetros de vivienda.
     */
    @GetMapping(value = "/stream", produces = "text/event-stream")
    public Flux<ServerSentEvent<Parameter>> streamUpdates() {
        return service.listenChanges()
                .filter(change -> change.type() != CatalogEventType.DELETED)
                .map(change -> ServerSentEvent.<Parameter>builder(change.payload())
                        .event(change.type().name())
                        .build());
    }

    public record ReservaExpiracionRequest(int horas) {
    }

    public record MaximoRecordatoriosRequest(int recordatorios) {
    }
}
