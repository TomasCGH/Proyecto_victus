package co.edu.uco.backendvictus.application.usecase.conjunto;

import java.util.UUID;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;

import co.edu.uco.backendvictus.application.dto.conjunto.ConjuntoResponse;
import co.edu.uco.backendvictus.application.mapper.ConjuntoApplicationMapper;
import co.edu.uco.backendvictus.application.port.out.conjunto.ConjuntoRepositoryPort;
import co.edu.uco.backendvictus.domain.model.conjunto.ConjuntoResidencial;

@Service
public class ListConjuntosUseCase {

    private final ConjuntoRepositoryPort conjuntoRepository;
    private final ConjuntoApplicationMapper mapper;

    public ListConjuntosUseCase(final ConjuntoRepositoryPort conjuntoRepository,
            final ConjuntoApplicationMapper mapper) {
        this.conjuntoRepository = conjuntoRepository;
        this.mapper = mapper;
    }

    public Flux<ConjuntoResponse> execute() {
        return conjuntoRepository.findAllWithNames().map(mapper::toResponse);
    }

    public Flux<ConjuntoResponse> executeFiltered(final UUID departamentoId, final UUID ciudadId, final String nombre) {
        final boolean hasNombre = nombre != null && !nombre.isBlank();
        Flux<ConjuntoResidencial> flux;

        if (departamentoId == null && ciudadId == null) {
            flux = hasNombre ? conjuntoRepository.findByNombre(nombre) : conjuntoRepository.findAllWithNames();
        } else if (departamentoId != null && ciudadId == null) {
            flux = conjuntoRepository.findByDepartamentoId(departamentoId);
        } else if (departamentoId == null) {
            flux = conjuntoRepository.findByCiudadId(ciudadId);
        } else {
            flux = conjuntoRepository.findByDepartamentoIdAndCiudadId(departamentoId, ciudadId);
        }

        if (hasNombre && (departamentoId != null || ciudadId != null)) {
            final String normalized = nombre.toLowerCase();
            flux = flux.filter(conjunto -> conjunto.getNombre().toLowerCase().contains(normalized));
        }

        return flux.map(mapper::toResponse);
    }
}
