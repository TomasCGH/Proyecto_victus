package co.edu.uco.backendvictus.infrastructure.secondary.mapper;

import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import co.edu.uco.backendvictus.domain.model.Ciudad;
import co.edu.uco.backendvictus.domain.model.Departamento;
import co.edu.uco.backendvictus.infrastructure.secondary.entity.CiudadJpaEntity;

@Mapper(componentModel = "spring", uses = DepartamentoEntityMapper.class)
public abstract class CiudadEntityMapper {

    @Autowired
    private DepartamentoEntityMapper departamentoEntityMapper;

    public abstract CiudadJpaEntity toEntity(Ciudad ciudad);

    public Ciudad toDomain(final CiudadJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        final Departamento departamento = departamentoEntityMapper.toDomain(entity.getDepartamento());
        return Ciudad.create(entity.getId(), entity.getNombre(), departamento, entity.isActivo());
    }
}
