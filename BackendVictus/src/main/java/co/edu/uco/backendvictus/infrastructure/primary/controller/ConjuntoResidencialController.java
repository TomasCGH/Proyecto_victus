package co.edu.uco.backendvictus.infrastructure.primary.controller;

import java.util.UUID;

import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import co.edu.uco.backendvictus.application.port.ConjuntoEventoPublisher;
import co.edu.uco.backendvictus.application.dto.conjunto.ConjuntoCreateRequest;
import co.edu.uco.backendvictus.application.dto.conjunto.ConjuntoResponse;
import co.edu.uco.backendvictus.application.dto.conjunto.ConjuntoUpdateRequest;
import co.edu.uco.backendvictus.application.dto.vivienda.ViviendaFilterRequest;
import co.edu.uco.backendvictus.application.dto.vivienda.ViviendaPageResponse;
import co.edu.uco.backendvictus.application.usecase.conjunto.CreateConjuntoUseCase;
import co.edu.uco.backendvictus.application.usecase.conjunto.DeleteConjuntoUseCase;
import co.edu.uco.backendvictus.application.usecase.conjunto.ListConjuntoUseCase;
import co.edu.uco.backendvictus.application.usecase.conjunto.UpdateConjuntoUseCase;
import co.edu.uco.backendvictus.application.usecase.vivienda.ListViviendaUseCase;
import co.edu.uco.backendvictus.crosscutting.helpers.DataSanitizer;
import co.edu.uco.backendvictus.infrastructure.primary.response.ApiSuccessResponse;
import co.edu.uco.backendvictus.infrastructure.primary.response.ApiResponseHelper; // ✅ import helper
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/uco-challenge/api/v1/conjuntos")
public class ConjuntoResidencialController {

    private final CreateConjuntoUseCase createConjuntoUseCase;
    private final ListConjuntoUseCase listConjuntoUseCase;
    private final UpdateConjuntoUseCase updateConjuntoUseCase;
    private final DeleteConjuntoUseCase deleteConjuntoUseCase;
    private final ConjuntoEventoPublisher eventoPublisher;
    private final ListViviendaUseCase listViviendaUseCase;

    public ConjuntoResidencialController(final CreateConjuntoUseCase createConjuntoUseCase,
                                         final ListConjuntoUseCase listConjuntoUseCase,
                                         final UpdateConjuntoUseCase updateConjuntoUseCase,
                                         final DeleteConjuntoUseCase deleteConjuntoUseCase,
                                         final ConjuntoEventoPublisher eventoPublisher,
                                         final ListViviendaUseCase listViviendaUseCase) {
        this.createConjuntoUseCase = createConjuntoUseCase;
        this.listConjuntoUseCase = listConjuntoUseCase;
        this.updateConjuntoUseCase = updateConjuntoUseCase;
        this.deleteConjuntoUseCase = deleteConjuntoUseCase;
        this.eventoPublisher = eventoPublisher;
        this.listViviendaUseCase = listViviendaUseCase;
    }

    @PostMapping
    public Mono<ResponseEntity<ApiSuccessResponse<ConjuntoResponse>>> crear(
            @Valid @RequestBody final ConjuntoCreateRequest request) {

        final ConjuntoCreateRequest sanitized = new ConjuntoCreateRequest(request.ciudadId(), request.administradorId(),
                DataSanitizer.sanitizeText(request.nombre()), DataSanitizer.sanitizeText(request.direccion()),
                DataSanitizer.sanitizeText(request.telefono()));

        return createConjuntoUseCase.execute(sanitized)
                .map(ApiSuccessResponse::of)
                .map(body -> ResponseEntity.status(HttpStatus.CREATED).body(body));
    }

    @GetMapping
    public Mono<ResponseEntity<ApiSuccessResponse<java.util.List<ConjuntoResponse>>>> listar(
            @RequestParam(name = "departamentoId", required = false) final UUID departamentoId,
            @RequestParam(name = "ciudadId", required = false) final UUID ciudadId) {
        return listConjuntoUseCase.executeFiltered(departamentoId, ciudadId)
                .collectList()
                .map(ApiSuccessResponse::of)
                .map(ResponseEntity::ok);
    }

    @GetMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<Flux<ServerSentEvent<ConjuntoEventoPublisher.Evento>>> streamEventos() {
        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl("no-cache");
        headers.add("X-Accel-Buffering", "no");
        headers.add("Connection", "keep-alive");

        Flux<ServerSentEvent<ConjuntoEventoPublisher.Evento>> body = eventoPublisher.stream()
                .map(evento -> ServerSentEvent.<ConjuntoEventoPublisher.Evento>builder()
                        .event(evento.tipo().name())
                        .data(evento)
                        .build());

        return ResponseEntity.ok().headers(headers).contentType(MediaType.TEXT_EVENT_STREAM).body(body);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ApiSuccessResponse<ConjuntoResponse>>> actualizar(
            @PathVariable("id") final UUID id,
            @Valid @RequestBody final ConjuntoUpdateRequest request) {

        final ConjuntoUpdateRequest sanitized = new ConjuntoUpdateRequest(
                id,
                request.ciudadId(),
                request.administradorId(),
                DataSanitizer.sanitizeText(request.nombre()),
                DataSanitizer.sanitizeText(request.direccion()),
                DataSanitizer.sanitizeText(request.telefono()));

        return updateConjuntoUseCase.execute(sanitized)
                .map(ApiSuccessResponse::of)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<ApiSuccessResponse<Void>>> eliminar(@PathVariable("id") final UUID id) {
        return deleteConjuntoUseCase.execute(id)
                .thenReturn(ApiResponseHelper.emptySuccess()) // ✅ sin null ni warnings de tipo
                .map(body -> ResponseEntity.ok(body));
    }

    @GetMapping("/{id}/viviendas")
    public Mono<ResponseEntity<ApiSuccessResponse<ViviendaPageResponse>>> listarViviendas(
            @PathVariable("id") final UUID conjuntoId,
            @RequestParam(name = "page", required = false) final Integer page,
            @RequestParam(name = "size", required = false) final Integer size) {
        return listViviendaUseCase.execute(new ViviendaFilterRequest(conjuntoId, null, null, null, page, size))
                .map(ApiSuccessResponse::of)
                .map(body -> ResponseEntity.ok(body));
    }
}
