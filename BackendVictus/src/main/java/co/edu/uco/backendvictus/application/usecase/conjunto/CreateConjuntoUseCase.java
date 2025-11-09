package co.edu.uco.backendvictus.application.usecase.conjunto;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import co.edu.uco.backendvictus.application.dto.conjunto.ConjuntoCreateRequest;
import co.edu.uco.backendvictus.application.dto.conjunto.ConjuntoResponse;
import co.edu.uco.backendvictus.application.mapper.ConjuntoApplicationMapper;
import co.edu.uco.backendvictus.application.usecase.UseCase;
import co.edu.uco.backendvictus.crosscutting.exception.ApplicationException;
import co.edu.uco.backendvictus.crosscutting.helpers.UuidGenerator;
import co.edu.uco.backendvictus.domain.model.Administrador;
import co.edu.uco.backendvictus.domain.model.Ciudad;
import co.edu.uco.backendvictus.domain.model.ConjuntoResidencial;
import co.edu.uco.backendvictus.domain.port.AdministradorRepository;
import co.edu.uco.backendvictus.domain.port.CiudadRepository;
import co.edu.uco.backendvictus.domain.port.ConjuntoResidencialRepository;

@Service
public class CreateConjuntoUseCase implements UseCase<ConjuntoCreateRequest, ConjuntoResponse> {

    private final ConjuntoResidencialRepository conjuntoRepository;
    private final CiudadRepository ciudadRepository;
    private final AdministradorRepository administradorRepository;
    private final ConjuntoApplicationMapper mapper;

    public CreateConjuntoUseCase(final ConjuntoResidencialRepository conjuntoRepository,
            final CiudadRepository ciudadRepository, final AdministradorRepository administradorRepository,
            final ConjuntoApplicationMapper mapper) {
        this.conjuntoRepository = conjuntoRepository;
        this.ciudadRepository = ciudadRepository;
        this.administradorRepository = administradorRepository;
        this.mapper = mapper;
    }

    @Override
    public Mono<ConjuntoResponse> execute(final ConjuntoCreateRequest request) {
        final Mono<Ciudad> ciudadMono = ciudadRepository.findById(request.ciudadId())
                .switchIfEmpty(Mono.error(new ApplicationException("Ciudad no encontrada")));
        final Mono<Administrador> administradorMono = administradorRepository.findById(request.administradorId())
                .switchIfEmpty(Mono.error(new ApplicationException("Administrador no encontrado")));

        return Mono.zip(ciudadMono, administradorMono)
                .map(tuple -> mapper.toDomain(UuidGenerator.generate(), request, tuple.getT1(), tuple.getT2()))
                .flatMap(conjuntoRepository::save)
                .map(mapper::toResponse);
    }
}
