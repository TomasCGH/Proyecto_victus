package co.edu.uco.backendvictus.infrastructure.secondary.entity;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("departamento")
public class DepartamentoEntity {

    @Id
    private final UUID id;

    @Column("pais_id")
    private final UUID paisId;

    @Column("nombre")
    private final String nombre;

    @Column("activo")
    private final boolean activo;

    @PersistenceCreator
    public DepartamentoEntity(final UUID id, final UUID paisId, final String nombre, final boolean activo) {
        this.id = id;
        this.paisId = paisId;
        this.nombre = nombre;
        this.activo = activo;
    }

    public UUID getId() {
        return id;
    }

    public UUID getPaisId() {
        return paisId;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean isActivo() {
        return activo;
    }
}
