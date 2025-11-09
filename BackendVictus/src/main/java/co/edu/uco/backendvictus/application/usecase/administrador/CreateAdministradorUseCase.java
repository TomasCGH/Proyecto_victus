package co.edu.uco.backendvictus.application.usecase.administrador;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uco.backendvictus.application.dto.administrador.AdministradorCreateRequest;
import co.edu.uco.backendvictus.application.dto.administrador.AdministradorResponse;
import co.edu.uco.backendvictus.application.mapper.AdministradorApplicationMapper;
import co.edu.uco.backendvictus.application.usecase.UseCase;
import co.edu.uco.backendvictus.domain.model.Administrador;
import co.edu.uco.backendvictus.domain.port.AdministradorRepository;

@Service
public class CreateAdministradorUseCase implements UseCase<AdministradorCreateRequest, AdministradorResponse> {

    private final AdministradorRepository administradorRepository;
    private final AdministradorApplicationMapper mapper;

    public CreateAdministradorUseCase(final AdministradorRepository administradorRepository,
            final AdministradorApplicationMapper mapper) {
        this.administradorRepository = administradorRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public AdministradorResponse execute(final AdministradorCreateRequest request) {
        final Administrador administrador = mapper.toDomain(UUID.randomUUID(), request);
        final Administrador persisted = administradorRepository.save(administrador);
        return mapper.toResponse(persisted);
    }
}
