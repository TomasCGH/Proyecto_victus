package co.edu.uco.backendvictus.application.usecase.vivienda;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import co.edu.uco.backendvictus.application.dto.vivienda.ViviendaCreateRequest;
import co.edu.uco.backendvictus.application.dto.vivienda.ViviendaResponse;
import co.edu.uco.backendvictus.application.mapper.ViviendaApplicationMapper;
import co.edu.uco.backendvictus.application.usecase.UseCase;
import co.edu.uco.backendvictus.crosscutting.exception.ApplicationException;
import co.edu.uco.backendvictus.crosscutting.helpers.UuidGenerator;
import co.edu.uco.backendvictus.domain.model.ConjuntoResidencial;
import co.edu.uco.backendvictus.domain.model.Vivienda;
import co.edu.uco.backendvictus.domain.port.ConjuntoResidencialRepository;
import co.edu.uco.backendvictus.domain.port.ViviendaRepository;

@Service
public class CreateViviendaUseCase implements UseCase<ViviendaCreateRequest, ViviendaResponse> {

    private final ViviendaRepository viviendaRepository;
    private final ConjuntoResidencialRepository conjuntoRepository;
    private final ViviendaApplicationMapper mapper;

    public CreateViviendaUseCase(final ViviendaRepository viviendaRepository,
            final ConjuntoResidencialRepository conjuntoRepository, final ViviendaApplicationMapper mapper) {
        this.viviendaRepository = viviendaRepository;
        this.conjuntoRepository = conjuntoRepository;
        this.mapper = mapper;
    }

    @Override
    public Mono<ViviendaResponse> execute(final ViviendaCreateRequest request) {
        return conjuntoRepository.findById(request.conjuntoId())
                .switchIfEmpty(Mono.error(new ApplicationException("Conjunto residencial no encontrado")))
                .flatMap(conjunto -> buildAndValidate(request, conjunto)
                        .flatMap(viviendaRepository::save))
                .map(mapper::toResponse);
    }

    private Mono<Vivienda> buildAndValidate(final ViviendaCreateRequest request,
            final ConjuntoResidencial conjunto) {
        final Vivienda vivienda = mapper.toDomain(null, request, conjunto);
        return viviendaRepository.findByConjuntoAndNumero(conjunto.getId(), vivienda.getNumero())
                .flatMap(existing -> Mono.<Vivienda>error(
                        new ApplicationException("Ya existe una vivienda con ese numero en el conjunto")))
                .switchIfEmpty(Mono.just(vivienda));
    }
}
