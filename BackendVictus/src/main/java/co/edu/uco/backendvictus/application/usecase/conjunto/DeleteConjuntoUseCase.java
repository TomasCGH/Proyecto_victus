package co.edu.uco.backendvictus.application.usecase.conjunto;

import java.util.UUID;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import co.edu.uco.backendvictus.crosscutting.exception.ApplicationException;
import co.edu.uco.backendvictus.domain.port.ConjuntoResidencialRepository;
import co.edu.uco.backendvictus.application.port.ConjuntoEventoPublisher;
import co.edu.uco.backendvictus.application.dto.conjunto.ConjuntoResponse;

@Service
public class DeleteConjuntoUseCase {

    private final ConjuntoResidencialRepository conjuntoRepository;
    private final ConjuntoEventoPublisher eventoPublisher;

    public DeleteConjuntoUseCase(final ConjuntoResidencialRepository conjuntoRepository, final ConjuntoEventoPublisher eventoPublisher) {
        this.conjuntoRepository = conjuntoRepository;
        this.eventoPublisher = eventoPublisher;
    }

    public Mono<Void> execute(final UUID id) {
        return conjuntoRepository.findById(id)
                .switchIfEmpty(Mono.error(new ApplicationException("Conjunto residencial no encontrado")))
                .flatMap(conjunto -> {
                    if (eventoPublisher != null) {
                        ConjuntoResponse payload = new ConjuntoResponse(conjunto.getId(), conjunto.getCiudad().getId(), conjunto.getAdministrador().getId(), conjunto.getNombre(), conjunto.getDireccion(), conjunto.getTelefono(), conjunto.getCiudad().getNombre(), conjunto.getCiudad().getDepartamento().getNombre());
                        return eventoPublisher.publish(new ConjuntoEventoPublisher.Evento(ConjuntoEventoPublisher.TipoEvento.DELETED, payload))
                                .then(conjuntoRepository.deleteById(id));
                    }
                    return conjuntoRepository.deleteById(id);
                });
    }
}
