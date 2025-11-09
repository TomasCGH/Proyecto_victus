package co.edu.uco.backendvictus.application.usecase.pais;

import java.util.List;

import org.springframework.stereotype.Service;

import co.edu.uco.backendvictus.application.dto.pais.PaisResponse;
import co.edu.uco.backendvictus.application.mapper.PaisApplicationMapper;
import co.edu.uco.backendvictus.domain.port.PaisRepository;

@Service
public class ListPaisUseCase {

    private final PaisRepository repository;
    private final PaisApplicationMapper mapper;

    public ListPaisUseCase(final PaisRepository repository, final PaisApplicationMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<PaisResponse> execute() {
        return mapper.toResponseList(repository.findAll());
    }
}
