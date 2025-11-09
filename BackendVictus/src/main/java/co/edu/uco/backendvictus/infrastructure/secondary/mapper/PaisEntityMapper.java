package co.edu.uco.backendvictus.infrastructure.secondary.mapper;

import org.mapstruct.Mapper;

import co.edu.uco.backendvictus.domain.model.Pais;
import co.edu.uco.backendvictus.infrastructure.secondary.entity.PaisJpaEntity;

@Mapper(componentModel = "spring")
public abstract class PaisEntityMapper {

    public abstract PaisJpaEntity toEntity(Pais pais);

    public Pais toDomain(final PaisJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return Pais.create(entity.getId(), entity.getNombre(), entity.isActivo());
    }
}
