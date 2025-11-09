package co.edu.uco.backendvictus.infrastructure.secondary.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import co.edu.uco.backendvictus.domain.model.Pais;
import co.edu.uco.backendvictus.domain.port.PaisRepository;
import co.edu.uco.backendvictus.infrastructure.secondary.entity.PaisJpaEntity;
import co.edu.uco.backendvictus.infrastructure.secondary.mapper.PaisEntityMapper;

@Repository
public class PaisRepositoryAdapter implements PaisRepository {

    private final PaisJpaRepository repository;
    private final PaisEntityMapper mapper;

    public PaisRepositoryAdapter(final PaisJpaRepository repository, final PaisEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Pais save(final Pais pais) {
        final PaisJpaEntity entity = mapper.toEntity(pais);
        final PaisJpaEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Pais> findById(final UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Pais> findAll() {
        return repository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(final UUID id) {
        repository.deleteById(id);
    }
}
