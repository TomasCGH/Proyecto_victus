package co.edu.uco.backendvictus.domain.model;

import java.util.UUID;

import co.edu.uco.backendvictus.crosscutting.helpers.ValidationUtils;
import co.edu.uco.backendvictus.domain.specification.ConjuntoAdministradorActivoSpecification;
import co.edu.uco.backendvictus.domain.specification.ConjuntoTieneCiudadSpecification;
import co.edu.uco.backendvictus.domain.specification.SpecificationValidator;

/**
 * Residential complex aggregate root.
 */
public final class ConjuntoResidencial {

    private final UUID id;
    private final String nombre;
    private final String direccion;
    private final Ciudad ciudad;
    private final Administrador administrador;

    private ConjuntoResidencial(final UUID id, final String nombre, final String direccion, final Ciudad ciudad,
            final Administrador administrador) {
        this.id = id; //ValidationUtils.validateUUID(id, "Id del conjunto residencial");
        this.nombre = ValidationUtils.validateRequiredText(nombre, "Nombre del conjunto", 150);
        this.direccion = ValidationUtils.validateRequiredText(direccion, "Direccion", 180);
        this.ciudad = ciudad;
        this.administrador = administrador;

        SpecificationValidator.check(ConjuntoAdministradorActivoSpecification.INSTANCE, this,
                "El conjunto residencial requiere un administrador activo");
        SpecificationValidator.check(ConjuntoTieneCiudadSpecification.INSTANCE, this,
                "El conjunto residencial debe pertenecer a una ciudad valida");
    }

    public static ConjuntoResidencial create(final UUID id, final String nombre, final String direccion,
            final Ciudad ciudad, final Administrador administrador) {
        return new ConjuntoResidencial(id, nombre, direccion, ciudad, administrador);
    }

    public ConjuntoResidencial update(final String nombre, final String direccion, final Ciudad ciudad,
            final Administrador administrador) {
        return new ConjuntoResidencial(this.id, nombre, direccion, ciudad, administrador);
    }

    public UUID getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public Ciudad getCiudad() {
        return ciudad;
    }

    public Administrador getAdministrador() {
        return administrador;
    }
}
