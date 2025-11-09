package co.edu.uco.backendvictus.application.usecase.pais;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uco.backendvictus.application.dto.pais.PaisCreateRequest;
import co.edu.uco.backendvictus.application.dto.pais.PaisResponse;
import co.edu.uco.backendvictus.application.mapper.PaisApplicationMapper;
import co.edu.uco.backendvictus.application.usecase.UseCase;
import co.edu.uco.backendvictus.domain.model.Pais;
import co.edu.uco.backendvictus.domain.port.PaisRepository;

@Service
public class CreatePaisUseCase implements UseCase<PaisCreateRequest, PaisResponse> {

    private final PaisRepository repository;
    private final PaisApplicationMapper mapper;

    public CreatePaisUseCase(final PaisRepository repository, final PaisApplicationMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public PaisResponse execute(final PaisCreateRequest request) {
        final Pais pais = mapper.toDomain(UUID.randomUUID(), request);
        final Pais persisted = repository.save(pais);
        return mapper.toResponse(persisted);
    }
}
