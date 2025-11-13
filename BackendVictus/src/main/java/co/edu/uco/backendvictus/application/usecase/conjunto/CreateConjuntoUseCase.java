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
import org.springframework.dao.IncorrectResultSizeDataAccessException;

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

        // Validaciones explícitas de entrada
        if (request.ciudadId() == null || request.administradorId() == null) {
            return messageClient.getMessage("validation.required.uuid")
                    .switchIfEmpty(Mono.just(new MessageClient.MessageResult(
                            "uuid.missing",
                            "Debes seleccionar ciudad y administrador antes de continuar.",
                            "backend-fallback")))
                    .flatMap(msg -> Mono.error(new ApplicationException(msg.clientMessage(), msg.source())));
        }

        if (request.telefono() == null || !request.telefono().matches("^[0-9]{1,10}$")) {
            return messageClient.getMessage("validation.format.telefono")
                    .switchIfEmpty(Mono.just(new MessageClient.MessageResult(
                            "telefono.invalid",
                            "El teléfono debe contener solo números y máximo 10 dígitos.",
                            "backend-fallback")))
                    .flatMap(msg -> Mono.error(new ApplicationException(msg.clientMessage(), msg.source())));
        }

        final Mono<Ciudad> ciudadMono = ciudadRepository.findById(request.ciudadId())
                .switchIfEmpty(Mono.error(new ApplicationException("Ciudad no encontrada", "backend")));
        final Mono<Administrador> administradorMono = administradorRepository.findById(request.administradorId())
                .switchIfEmpty(Mono.error(new ApplicationException("Administrador no encontrado", "backend")));

        return Mono.zip(ciudadMono, administradorMono)
                .flatMap(tuple -> {
                    final Ciudad ciudad = tuple.getT1();
                    final Administrador admin = tuple.getT2();

                    // Verificar duplicado por teléfono (puede retornar múltiples registros)
                    return conjuntoRepository.findAllByTelefono(request.telefono())
                            .collectList()
                            .flatMap(existentes -> {
                                if (existentes != null && !existentes.isEmpty()) {
                                    return messageClient.getMessage("domain.conjunto.telefono.duplicated")
                                            .switchIfEmpty(Mono.just(new MessageClient.MessageResult(
                                                    "duplicate.phone",
                                                    "Ya existe un conjunto residencial registrado con ese teléfono.",
                                                    "backend-fallback")))
                                            .flatMap(msg -> Mono.<ConjuntoResidencial>error(new ApplicationException(msg.clientMessage(), msg.source())));
                                }

                                // Verificar duplicado por nombre y ciudad
                                return conjuntoRepository.findByCiudadAndNombre(ciudad.getId(), request.nombre())
                                        .flatMap(existing -> messageClient.getMessage("domain.conjunto.nombre.duplicated")
                                                .switchIfEmpty(Mono.just(new MessageClient.MessageResult(
                                                        "duplicate.name",
                                                        "Ya existe un conjunto con ese nombre en la misma ciudad.",
                                                        "backend-fallback")))
                                                .flatMap(msg -> Mono.<ConjuntoResidencial>error(new ApplicationException(msg.clientMessage(), msg.source()))))
                                         .switchIfEmpty(Mono.defer(() -> {
                                             final ConjuntoResidencial nuevo = mapper.toDomain(null, request, ciudad, admin);
                                             return conjuntoRepository.save(nuevo);
                                         }));
                            });
                })
                .map(mapper::toResponse)
                .onErrorResume(Exception.class, ex -> {
                    // Si ya es una ApplicationException o está envuelta, la dejamos pasar sin mapear
                    Throwable cause = ex;
                    while (cause != null) {
                        if (cause instanceof ApplicationException) {
                            return Mono.error(cause);
                        }
                        cause = cause.getCause();
                    }
                    LOGGER.error("Error inesperado en CreateConjuntoUseCase: {}", ex.getMessage());
                    return messageClient.getMessage("domain.general.error")
                            .switchIfEmpty(Mono.just(new MessageClient.MessageResult(
                                    "unexpected.error",
                                    "Ocurrió un error inesperado. Intenta nuevamente.",
                                    "backend-fallback")))
                            .flatMap(msg -> Mono.error(new ApplicationException(msg.clientMessage(), msg.source())));
                });
    }

    private Mono<ConjuntoResidencial> duplicateConjuntoError() {
        return messageClient.getMessage("domain.conjunto.nombre.duplicated")
                .switchIfEmpty(Mono.just(new MessageClient.MessageResult(
                        "Duplicate residential complex detected.",
                        "Ya existe un conjunto residencial registrado con ese nombre.",
                        "backend-default")))
                .flatMap(msg -> {
                    LOGGER.warn("Creación de conjunto duplicado. Technical='{}'", msg.technicalMessage());
                    return Mono.error(new ApplicationException(msg.clientMessage(), msg.source()));
                });
    }

    private Mono<ConjuntoResidencial> duplicateConjuntoTelefonoError() {
        return messageClient.getMessage("domain.conjunto.telefono.duplicated")
                .switchIfEmpty(Mono.just(new MessageClient.MessageResult(
                        "Duplicate residential complex detected (phone).",
                        "Ya existe un conjunto residencial registrado con ese teléfono.",
                        "backend-default")))
                .flatMap(msg -> {
                    LOGGER.warn("Creación de conjunto duplicado por teléfono. Technical='{}'", msg.technicalMessage());
                    return Mono.error(new ApplicationException(msg.clientMessage(), msg.source()));
                });
    }
}
