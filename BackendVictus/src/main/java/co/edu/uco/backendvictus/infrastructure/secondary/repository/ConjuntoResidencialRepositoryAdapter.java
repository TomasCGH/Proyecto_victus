package co.edu.uco.backendvictus.infrastructure.secondary.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import co.edu.uco.backendvictus.domain.model.ConjuntoResidencial;
import co.edu.uco.backendvictus.domain.port.ConjuntoResidencialRepository;
import co.edu.uco.backendvictus.infrastructure.secondary.entity.AdministradorJpaEntity;
import co.edu.uco.backendvictus.infrastructure.secondary.entity.CiudadJpaEntity;
import co.edu.uco.backendvictus.infrastructure.secondary.entity.ConjuntoResidencialJpaEntity;
import co.edu.uco.backendvictus.infrastructure.secondary.mapper.ConjuntoResidencialEntityMapper;

@Repository
public class ConjuntoResidencialRepositoryAdapter implements ConjuntoResidencialRepository {

    private final ConjuntoResidencialJpaRepository conjuntoJpaRepository;
    private final CiudadJpaRepository ciudadJpaRepository;
    private final AdministradorJpaRepository administradorJpaRepository;
    private final ConjuntoResidencialEntityMapper mapper;

    public ConjuntoResidencialRepositoryAdapter(final ConjuntoResidencialJpaRepository conjuntoJpaRepository,
            final CiudadJpaRepository ciudadJpaRepository, final AdministradorJpaRepository administradorJpaRepository,
            final ConjuntoResidencialEntityMapper mapper) {
        this.conjuntoJpaRepository = conjuntoJpaRepository;
        this.ciudadJpaRepository = ciudadJpaRepository;
        this.administradorJpaRepository = administradorJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public ConjuntoResidencial save(final ConjuntoResidencial conjuntoResidencial) {
        final CiudadJpaEntity ciudad = ciudadJpaRepository.getReferenceById(conjuntoResidencial.getCiudad().getId());
        final AdministradorJpaEntity administrador =
                administradorJpaRepository.getReferenceById(conjuntoResidencial.getAdministrador().getId());
        final ConjuntoResidencialJpaEntity entity = mapper.toEntity(conjuntoResidencial);
        entity.setCiudad(ciudad);
        entity.setAdministrador(administrador);
        final ConjuntoResidencialJpaEntity saved = conjuntoJpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<ConjuntoResidencial> findById(final UUID id) {
        return conjuntoJpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<ConjuntoResidencial> findAll() {
        return conjuntoJpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(final UUID id) {
        conjuntoJpaRepository.deleteById(id);
    }
}
