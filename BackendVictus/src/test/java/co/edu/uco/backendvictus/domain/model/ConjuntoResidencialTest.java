package co.edu.uco.backendvictus.domain.model;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import co.edu.uco.backendvictus.crosscutting.exception.DomainException;

class ConjuntoResidencialTest {

    @Test
    void shouldFailWhenCityIsMissing() {
        final Administrador administrador = Administrador.create(UUID.randomUUID(), "Ana", null, "Lopez", null,
                "ana@uco.edu", "1234567", true);

        assertThrows(DomainException.class, () -> ConjuntoResidencial.create(UUID.randomUUID(), "Conjunto Central",
                "Cra 10 #20", null, administrador, true));
    }

    @Test
    void shouldFailWhenAdministratorIsInactive() {
        final Pais pais = Pais.create(UUID.randomUUID(), "Colombia", true);
        final Departamento departamento = Departamento.create(UUID.randomUUID(), "Antioquia", pais, true);
        final Ciudad ciudad = Ciudad.create(UUID.randomUUID(), "Medellin", departamento, true);
        final Administrador administrador = Administrador.create(UUID.randomUUID(), "Pedro", "Jose", "Gomez", null,
                "pedro@uco.edu", "9876543", false);

        assertThrows(DomainException.class, () -> ConjuntoResidencial.create(UUID.randomUUID(), "Conjunto Central",
                "Cra 10 #20", ciudad, administrador, true));
    }
}
