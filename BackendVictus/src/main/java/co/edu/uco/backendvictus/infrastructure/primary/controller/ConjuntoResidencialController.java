package co.edu.uco.backendvictus.infrastructure.primary.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.uco.backendvictus.application.dto.conjunto.ConjuntoCreateRequest;
import co.edu.uco.backendvictus.application.dto.conjunto.ConjuntoResponse;
import co.edu.uco.backendvictus.application.dto.conjunto.ConjuntoUpdateRequest;
import co.edu.uco.backendvictus.application.usecase.conjunto.CreateConjuntoUseCase;
import co.edu.uco.backendvictus.application.usecase.conjunto.DeleteConjuntoUseCase;
import co.edu.uco.backendvictus.application.usecase.conjunto.ListConjuntoUseCase;
import co.edu.uco.backendvictus.application.usecase.conjunto.UpdateConjuntoUseCase;
import co.edu.uco.backendvictus.crosscutting.helpers.DataSanitizer;
import co.edu.uco.backendvictus.infrastructure.primary.response.ApiSuccessResponse;
import co.edu.uco.backendvictus.infrastructure.primary.response.ApiResponseHelper; // ✅ import helper
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/uco-challenge/api/v1/conjuntos")
public class ConjuntoResidencialController {

    private final CreateConjuntoUseCase createConjuntoUseCase;
    private final ListConjuntoUseCase listConjuntoUseCase;
    private final UpdateConjuntoUseCase updateConjuntoUseCase;
    private final DeleteConjuntoUseCase deleteConjuntoUseCase;

    public ConjuntoResidencialController(final CreateConjuntoUseCase createConjuntoUseCase,
                                         final ListConjuntoUseCase listConjuntoUseCase,
                                         final UpdateConjuntoUseCase updateConjuntoUseCase,
                                         final DeleteConjuntoUseCase deleteConjuntoUseCase) {
        this.createConjuntoUseCase = createConjuntoUseCase;
        this.listConjuntoUseCase = listConjuntoUseCase;
        this.updateConjuntoUseCase = updateConjuntoUseCase;
        this.deleteConjuntoUseCase = deleteConjuntoUseCase;
    }

    @PostMapping
    public Mono<ResponseEntity<ApiSuccessResponse<ConjuntoResponse>>> crear(
            @RequestBody final ConjuntoCreateRequest request) {

        final ConjuntoCreateRequest sanitized = new ConjuntoCreateRequest(
                request.ciudadId(),
                request.administradorId(),
                DataSanitizer.sanitizeText(request.nombre()),
                DataSanitizer.sanitizeText(request.direccion()),
                request.activo());

        return createConjuntoUseCase.execute(sanitized)
                .map(ApiSuccessResponse::of)
                .map(body -> ResponseEntity.status(HttpStatus.CREATED).body(body));
    }

    @GetMapping
    public Mono<ResponseEntity<ApiSuccessResponse<java.util.List<ConjuntoResponse>>>> listar() {
        return listConjuntoUseCase.execute()
                .collectList()
                .map(ApiSuccessResponse::of)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ApiSuccessResponse<ConjuntoResponse>>> actualizar(
            @PathVariable("id") final UUID id,
            @RequestBody final ConjuntoUpdateRequest request) {

        final ConjuntoUpdateRequest sanitized = new ConjuntoUpdateRequest(
                id,
                request.ciudadId(),
                request.administradorId(),
                DataSanitizer.sanitizeText(request.nombre()),
                DataSanitizer.sanitizeText(request.direccion()),
                request.activo());

        return updateConjuntoUseCase.execute(sanitized)
                .map(ApiSuccessResponse::of)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<ApiSuccessResponse<Void>>> eliminar(@PathVariable("id") final UUID id) {
        return deleteConjuntoUseCase.execute(id)
                .thenReturn(ApiResponseHelper.emptySuccess()) // ✅ sin null ni warnings de tipo
                .map(ResponseEntity::ok);
    }
}
