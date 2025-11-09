package co.edu.uco.backendvictus.infrastructure.secondary.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import co.edu.uco.backendvictus.domain.model.Administrador;
import co.edu.uco.backendvictus.domain.model.Ciudad;
import co.edu.uco.backendvictus.domain.model.ConjuntoResidencial;
import co.edu.uco.backendvictus.infrastructure.secondary.entity.ConjuntoResidencialJpaEntity;

@Mapper(componentModel = "spring", uses = {CiudadEntityMapper.class, AdministradorEntityMapper.class})
public abstract class ConjuntoResidencialEntityMapper {

    @Autowired
    private CiudadEntityMapper ciudadEntityMapper;

    @Autowired
    private AdministradorEntityMapper administradorEntityMapper;

    @Mapping(target = "ciudad", source = "ciudad")
    @Mapping(target = "administrador", source = "administrador")
    public abstract ConjuntoResidencialJpaEntity toEntity(ConjuntoResidencial conjuntoResidencial);

    public ConjuntoResidencial toDomain(final ConjuntoResidencialJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        final Ciudad ciudad = ciudadEntityMapper.toDomain(entity.getCiudad());
        final Administrador administrador = administradorEntityMapper.toDomain(entity.getAdministrador());
        return ConjuntoResidencial.create(entity.getId(), entity.getNombre(), entity.getDireccion(), ciudad,
                administrador, entity.isActivo());
    }
}
