package co.edu.uco.backendvictus.infrastructure.secondary.entity;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.annotation.PersistenceCreator;

@Table("pais")
public class PaisEntity {

    @Id
    private final UUID id;

    @Column("nombre")
    private final String nombre;

    @Column("activo")
    private final boolean activo;

    @PersistenceCreator
    public PaisEntity(final UUID id, final String nombre, final boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.activo = activo;
    }

    public UUID getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean isActivo() {
        return activo;
    }
}
