package co.edu.uco.backendvictus.infrastructure.secondary.mapper;

import org.mapstruct.Mapper;

import co.edu.uco.backendvictus.domain.model.Administrador;
import co.edu.uco.backendvictus.infrastructure.secondary.entity.AdministradorJpaEntity;

@Mapper(componentModel = "spring")
public abstract class AdministradorEntityMapper {

    public abstract AdministradorJpaEntity toEntity(Administrador administrador);

    public Administrador toDomain(final AdministradorJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return Administrador.create(entity.getId(), entity.getPrimerNombre(), entity.getSegundoNombres(),
                entity.getPrimerApellido(), entity.getSegundoApellido(), entity.getEmail(), entity.getTelefono(),
                entity.isActivo());
    }
}
