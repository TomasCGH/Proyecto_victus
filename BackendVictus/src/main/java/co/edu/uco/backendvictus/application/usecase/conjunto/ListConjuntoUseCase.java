package co.edu.uco.backendvictus.application.usecase.conjunto;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;

import co.edu.uco.backendvictus.application.dto.conjunto.ConjuntoResponse;
import co.edu.uco.backendvictus.application.mapper.ConjuntoApplicationMapper;
import co.edu.uco.backendvictus.domain.port.ConjuntoResidencialRepository;

@Service
public class ListConjuntoUseCase {

    private final ConjuntoResidencialRepository conjuntoRepository;
    private final ConjuntoApplicationMapper mapper;

    public ListConjuntoUseCase(final ConjuntoResidencialRepository conjuntoRepository,
            final ConjuntoApplicationMapper mapper) {
        this.conjuntoRepository = conjuntoRepository;
        this.mapper = mapper;
    }

    public Flux<ConjuntoResponse> execute() {
        return conjuntoRepository.findAll().map(mapper::toResponse);
    }
}
