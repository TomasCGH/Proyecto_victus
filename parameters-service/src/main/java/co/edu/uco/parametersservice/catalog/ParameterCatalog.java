package co.edu.uco.parametersservice.catalog;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Catálogo en memoria que mantiene configuraciones para la gestión de
 * viviendas. Los parámetros permiten ajustar reglas de negocio como tiempos de
 * reserva, recordatorios e información de contacto para la administración de
 * conjuntos residenciales.
 */
public final class ParameterCatalog {

    private static final Map<String, Parameter> PARAMETERS = new ConcurrentHashMap<>();

    static {
        register("notification.vivienda.administrator.email", "admin@uco.edu.co");
        register("notification.vivienda.reserva.template",
                "Hola %s, la vivienda %s ha sido reservada en el conjunto %s. Por favor confirme la disponibilidad.");
        register("notification.vivienda.mantenimiento.template",
                "Estimado %s, la vivienda %s ingresará en mantenimiento el %s.");
        register("gestion.vivienda.reserva.expiracionHoras", "24");
        register("gestion.vivienda.inspeccion.maxRecordatorios", "3");
        register("gestion.vivienda.estado.permitidos", "DISPONIBLE,OCUPADA,EN_MANTENIMIENTO");
        register("gestion.vivienda.tipo.permitidos", "APARTAMENTO,CASA,LOCAL");
        register("gestion.vivienda.numero.longitudMaxima", "10");
        register("gestion.vivienda.conjunto.nombre.longitudMaxima", "80");
        register("gestion.vivienda.reporte.pendientes.limite", "50");
    }

    private ParameterCatalog() {
        // Evitar instanciación
    }

    public static Parameter getParameterValue(String key) {
        return PARAMETERS.get(key);
    }

    public static void synchronizeParameterValue(Parameter parameter) {
        register(parameter.getKey(), parameter.getValue());
    }

    public static Parameter removeParameter(String key) {
        return PARAMETERS.remove(key);
    }

    public static Map<String, Parameter> getAllParameters() {
        return PARAMETERS;
    }

    private static void register(final String key, final String value) {
        PARAMETERS.put(key, new Parameter(key, value));
    }
}
