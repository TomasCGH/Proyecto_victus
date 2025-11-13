package co.edu.uco.backendvictus.application.usecase.conjunto;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import co.edu.uco.backendvictus.application.dto.conjunto.ConjuntoCreateRequest;
import co.edu.uco.backendvictus.application.dto.conjunto.ConjuntoResponse;
import co.edu.uco.backendvictus.application.mapper.ConjuntoApplicationMapper;
import co.edu.uco.backendvictus.application.usecase.UseCase;
import co.edu.uco.backendvictus.crosscutting.exception.ApplicationException;
import co.edu.uco.backendvictus.crosscutting.helpers.LoggerHelper;
import co.edu.uco.backendvictus.crosscutting.helpers.UuidGenerator;
import co.edu.uco.backendvictus.domain.model.Administrador;
import co.edu.uco.backendvictus.domain.model.Ciudad;
import co.edu.uco.backendvictus.domain.model.ConjuntoResidencial;
import co.edu.uco.backendvictus.domain.port.AdministradorRepository;
import co.edu.uco.backendvictus.domain.port.CiudadRepository;
import co.edu.uco.backendvictus.domain.port.ConjuntoResidencialRepository;
import co.edu.uco.backendvictus.infrastructure.secondary.client.MessageClient;
import co.edu.uco.backendvictus.infrastructure.secondary.client.ParameterClient;

@Service
public class CreateConjuntoUseCase implements UseCase<ConjuntoCreateRequest, ConjuntoResponse> {

    private static final Logger LOGGER = LoggerHelper.getLogger(CreateConjuntoUseCase.class);

    private final ConjuntoResidencialRepository conjuntoRepository;
    private final CiudadRepository ciudadRepository;
    private final AdministradorRepository administradorRepository;
    private final ConjuntoApplicationMapper mapper;
    private final MessageClient messageClient;
    private final ParameterClient parameterClient;

    public CreateConjuntoUseCase(final ConjuntoResidencialRepository conjuntoRepository,
            final CiudadRepository ciudadRepository, final AdministradorRepository administradorRepository,
            final ConjuntoApplicationMapper mapper) {
        this(conjuntoRepository, ciudadRepository, administradorRepository, mapper, MessageClient.fallback(), ParameterClient.fallback());
    }

    @Autowired
    public CreateConjuntoUseCase(final ConjuntoResidencialRepository conjuntoRepository,
            final CiudadRepository ciudadRepository, final AdministradorRepository administradorRepository,
            final ConjuntoApplicationMapper mapper, final MessageClient messageClient,
            final ParameterClient parameterClient) {
        this.conjuntoRepository = conjuntoRepository;
        this.ciudadRepository = ciudadRepository;
        this.administradorRepository = administradorRepository;
        this.mapper = mapper;
        this.messageClient = messageClient;
        this.parameterClient = parameterClient;
    }

    @Override
    public Mono<ConjuntoResponse> execute(final ConjuntoCreateRequest request) {
        final Mono<Ciudad> ciudadMono = ciudadRepository.findById(request.ciudadId())
                .switchIfEmpty(Mono.error(new ApplicationException("Ciudad no encontrada")));
        final Mono<Administrador> administradorMono = administradorRepository.findById(request.administradorId())
                .switchIfEmpty(Mono.error(new ApplicationException("Administrador no encontrado")));

        return parameterClient.get("conjunto.max.limit")
                .doOnNext(p -> LOGGER.info("ParameterService → parámetro 'conjunto.max.limit' = {} (source={})", p.value(), p.source()))
                .onErrorResume(ex -> Mono.empty())
                .then(Mono.zip(ciudadMono, administradorMono))
                .flatMap(tuple -> {
                    final Ciudad ciudad = tuple.getT1();
                    final Administrador admin = tuple.getT2();
                    // Validación de duplicado por (ciudadId, nombre normalizado)
                    return conjuntoRepository.findByCiudadAndNombre(ciudad.getId(), request.nombre())
                            .flatMap(existing -> messageClient.getMessage("domain.conjunto.nombre.duplicated")
                                    .flatMap(msg -> {
                                        LOGGER.warn("Creación de conjunto duplicado. Technical='{}'", msg.technicalMessage());
                                        return Mono.<ConjuntoResidencial>error(new ApplicationException(msg.clientMessage(), msg.source()));
                                    })
                            )
                            .switchIfEmpty(Mono.defer(() -> {
                                final ConjuntoResidencial nuevo = mapper.toDomain(UuidGenerator.generate(), request, ciudad, admin);
                                return conjuntoRepository.save(nuevo);
                            }));
                })
                .map(mapper::toResponse);
    }
}
