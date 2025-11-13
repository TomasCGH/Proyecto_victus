package co.edu.uco.backendvictus.infrastructure.secondary.repository;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;

import co.edu.uco.backendvictus.infrastructure.secondary.entity.ConjuntoResidencialEntity;

public interface ConjuntoResidencialR2dbcRepository
        extends ReactiveCrudRepository<ConjuntoResidencialEntity, UUID> {

    Mono<ConjuntoResidencialEntity> findByCiudadIdAndNombre(UUID ciudadId, String nombre);

    @Query("SELECT * FROM conjunto_residencial WHERE telefono = :telefono")
    Flux<ConjuntoResidencialEntity> findAllByTelefono(@Param("telefono") String telefono);
}
