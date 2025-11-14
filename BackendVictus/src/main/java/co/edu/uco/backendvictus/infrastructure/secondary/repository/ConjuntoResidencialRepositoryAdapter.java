package co.edu.uco.backendvictus.infrastructure.secondary.repository;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import co.edu.uco.backendvictus.domain.model.ConjuntoResidencial;
import co.edu.uco.backendvictus.domain.port.AdministradorRepository;
import co.edu.uco.backendvictus.domain.port.CiudadRepository;
import co.edu.uco.backendvictus.domain.port.ConjuntoResidencialRepository;
import co.edu.uco.backendvictus.infrastructure.secondary.entity.ConjuntoResidencialEntity;
import co.edu.uco.backendvictus.infrastructure.secondary.mapper.ConjuntoResidencialEntityMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class ConjuntoResidencialRepositoryAdapter implements ConjuntoResidencialRepository {

    private final ConjuntoResidencialR2dbcRepository repository;
    private final ConjuntoResidencialEntityMapper mapper;
    private final CiudadRepository ciudadRepository;
    private final AdministradorRepository administradorRepository;

    public ConjuntoResidencialRepositoryAdapter(final ConjuntoResidencialR2dbcRepository repository,
            final ConjuntoResidencialEntityMapper mapper, final CiudadRepository ciudadRepository,
            final AdministradorRepository administradorRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.ciudadRepository = ciudadRepository;
        this.administradorRepository = administradorRepository;
    }

    @Override
    public Mono<ConjuntoResidencial> save(final ConjuntoResidencial conjuntoResidencial) {
        return repository.save(mapper.toEntity(conjuntoResidencial))
                .flatMap(this::toDomain)
                .onErrorMap(org.springframework.dao.DataIntegrityViolationException.class,
                        e -> new co.edu.uco.backendvictus.crosscutting.exception.ApplicationException(
                                "El teléfono ya está registrado en otro conjunto residencial", "database-constraint"));
    }

    @Override
    public Mono<ConjuntoResidencial> findById(final UUID id) {
        return repository.findById(id).flatMap(this::toDomain);
    }

    @Override
    public Flux<ConjuntoResidencial> findAll() {
        return repository.findAll().flatMap(this::toDomain);
    }

    @Override
    public Mono<Void> deleteById(final UUID id) {
        return repository.deleteById(id);
    }

    @Override
    public Mono<ConjuntoResidencial> findByCiudadAndNombre(final UUID ciudadId, final String nombre) {
        return repository.findByCiudadIdAndNombre(ciudadId, nombre)
                .flatMap(this::toDomain);
    }

    @Override
    public Flux<ConjuntoResidencial> findAllByTelefono(final String telefono) {
        return repository.findAllByTelefono(telefono)
                .flatMap(this::toDomain);
    }

    @Override
    public Flux<ConjuntoResidencial> findByCiudadId(final UUID ciudadId) {
        return repository.findByCiudadId(ciudadId).flatMap(this::toDomain);
    }

    @Override
    public Flux<ConjuntoResidencial> findByDepartamentoId(final UUID departamentoId) {
        return repository.findByDepartamentoId(departamentoId).flatMap(this::toDomain);
    }

    @Override
    public Flux<ConjuntoResidencial> findByCiudadIdAndDepartamentoId(final UUID ciudadId, final UUID departamentoId) {
        return repository.findByCiudadIdAndDepartamentoId(ciudadId, departamentoId).flatMap(this::toDomain);
    }

    private Mono<ConjuntoResidencial> toDomain(final ConjuntoResidencialEntity entity) {
        final Mono<co.edu.uco.backendvictus.domain.model.Ciudad> ciudadMono = ciudadRepository
                .findById(entity.getCiudadId());
        final Mono<co.edu.uco.backendvictus.domain.model.Administrador> administradorMono = administradorRepository
                .findById(entity.getAdministradorId());

        return Mono.zip(ciudadMono, administradorMono)
                .map(tuple -> mapper.toDomain(entity, tuple.getT1(), tuple.getT2()));
    }
}
