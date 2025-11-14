package co.edu.uco.backendvictus.domain.port;

import java.util.UUID;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import co.edu.uco.backendvictus.domain.model.ConjuntoResidencial;

public interface ConjuntoResidencialRepository {

    Mono<ConjuntoResidencial> save(ConjuntoResidencial conjuntoResidencial);

    Mono<ConjuntoResidencial> findById(UUID id);

    Flux<ConjuntoResidencial> findAll();

    Mono<Void> deleteById(UUID id);

    Mono<ConjuntoResidencial> findByCiudadAndNombre(UUID ciudadId, String nombre);

    // Retorna todos los registros que coinciden con el teléfono para evitar IncorrectResultSizeDataAccessException
    Flux<ConjuntoResidencial> findAllByTelefono(String telefono);

    // Nuevos métodos de filtrado
    Flux<ConjuntoResidencial> findByCiudadId(UUID ciudadId);

    Flux<ConjuntoResidencial> findByDepartamentoId(UUID departamentoId);

    Flux<ConjuntoResidencial> findByCiudadIdAndDepartamentoId(UUID ciudadId, UUID departamentoId);
}
