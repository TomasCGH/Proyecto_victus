package co.edu.uco.backendvictus.application.usecase.conjunto;

import java.util.UUID;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import co.edu.uco.backendvictus.crosscutting.exception.ApplicationException;
import co.edu.uco.backendvictus.domain.port.ConjuntoResidencialRepository;

@Service
public class DeleteConjuntoUseCase {

    private final ConjuntoResidencialRepository conjuntoRepository;

    public DeleteConjuntoUseCase(final ConjuntoResidencialRepository conjuntoRepository) {
        this.conjuntoRepository = conjuntoRepository;
    }

    public Mono<Void> execute(final UUID id) {
        return conjuntoRepository.findById(id)
                .switchIfEmpty(Mono.error(new ApplicationException("Conjunto residencial no encontrado")))
                .then(conjuntoRepository.deleteById(id));
    }
}
