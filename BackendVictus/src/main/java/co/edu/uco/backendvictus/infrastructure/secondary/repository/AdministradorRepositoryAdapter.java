package co.edu.uco.backendvictus.infrastructure.secondary.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import co.edu.uco.backendvictus.domain.model.Administrador;
import co.edu.uco.backendvictus.domain.port.AdministradorRepository;
import co.edu.uco.backendvictus.infrastructure.secondary.entity.AdministradorJpaEntity;
import co.edu.uco.backendvictus.infrastructure.secondary.mapper.AdministradorEntityMapper;

@Repository
public class AdministradorRepositoryAdapter implements AdministradorRepository {

    private final AdministradorJpaRepository administradorJpaRepository;
    private final AdministradorEntityMapper mapper;

    public AdministradorRepositoryAdapter(final AdministradorJpaRepository administradorJpaRepository,
            final AdministradorEntityMapper mapper) {
        this.administradorJpaRepository = administradorJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Administrador save(final Administrador administrador) {
        final AdministradorJpaEntity entity = mapper.toEntity(administrador);
        final AdministradorJpaEntity saved = administradorJpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Administrador> findById(final UUID id) {
        return administradorJpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Administrador> findAll() {
        return administradorJpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(final UUID id) {
        administradorJpaRepository.deleteById(id);
    }
}
