package co.edu.uco.messageservice.catalog;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Catálogo en memoria que expone mensajes de negocio relacionados con el ciclo
 * de vida de los conjuntos residenciales. Los mensajes son consumidos por otros
 * microservicios para mantener consistencia textual en notificaciones y
 * respuestas HTTP.
 */
public final class MessageCatalog {

    private static final Map<String, Message> MESSAGES = new ConcurrentHashMap<>();

    static {
        // Excepciones generales orientadas a conjunto residencial
        register("exception.general.unexpected",
                "Ha ocurrido un error inesperado al gestionar el conjunto residencial. Intente nuevamente más tarde.");
        register("exception.general.technical",
                "Se produjo un error interno al procesar la operación de conjunto residencial.");
        register("exception.general.request",
                "No fue posible procesar la solicitud de conjunto residencial con la información recibida.");

        // Registro/validaciones de conjunto
        register("register.conjunto.success", "Conjunto residencial registrado exitosamente.");
        register("register.conjunto.validation.nombre.required", "El nombre del conjunto residencial es obligatorio.");
        register("register.conjunto.validation.nombre.length", "El nombre del conjunto residencial no puede superar 80 caracteres.");
        register("register.conjunto.validation.ciudad.required", "Debe asignarse una ciudad válida.");
        register("register.conjunto.validation.administrador.required", "Debe asignarse un administrador válido.");

        // Operaciones de consulta para conjuntos
        register("list.conjuntos.success", "Conjuntos residenciales obtenidos exitosamente.");
        register("get.conjunto.success", "Conjunto residencial obtenido exitosamente.");
        register("search.conjuntos.success", "Conjuntos residenciales filtrados exitosamente.");
        register("delete.conjunto.success", "Conjunto residencial eliminado exitosamente.");
        register("list.conjuntos.validation.page.negative", "La página solicitada no puede ser negativa.");
        register("list.conjuntos.validation.size.invalid", "El tamaño de página debe estar entre 1 y 50 registros.");

        // Mensajes de dominio de conjunto
        register("domain.conjunto.nombre.duplicated", "Ya existe un conjunto residencial registrado con ese nombre en la ciudad.");
        register("domain.conjunto.create.success", "El conjunto residencial fue creado correctamente.");
        register("domain.conjunto.update.success", "La información del conjunto residencial fue actualizada correctamente.");
        register("domain.conjunto.delete.success", "El conjunto residencial fue eliminado correctamente.");

        // Notificaciones (ejemplos orientativos para conjunto)
        register("notification.conjunto.creado",
                "Se creó el conjunto residencial {conjunto} en la ciudad {ciudad}.");
        register("notification.conjunto.actualizado",
                "Se actualizó la información del conjunto residencial {conjunto}.");

        // Mensajes de infraestructura y aplicación específicos de conjunto
        register("application.unexpectedError.user",
                "Se presentó un error inesperado al consultar los mensajes de conjunto. Intente nuevamente.");
        register("application.unexpectedError.technical", "UNEXPECTED_CONJUNTO_ERROR - revise trazas y causa raíz.");
        register("infrastructure.messageService.unavailable.user",
                "El servicio de mensajes de conjuntos residenciales no está disponible en este momento.");
        register("infrastructure.messageService.unavailable.technical", "CONJUNTO_MESSAGE_SERVICE_UNAVAILABLE");
        register("infrastructure.parameterService.unavailable.user",
                "El servicio de parámetros de conjunto residencial no está disponible.");
        register("infrastructure.parameterService.unavailable.technical", "CONJUNTO_PARAMETER_SERVICE_UNAVAILABLE");
        register("infrastructure.parameterService.invalidResponse.user",
                "Se recibió un valor de parámetro de conjunto residencial inválido.");
        register("infrastructure.parameterService.invalidResponse.technical", "CONJUNTO_PARAMETER_INVALID_RESPONSE");

        // Validaciones generales de request
        register("request.payload.invalid", "El cuerpo de la solicitud de conjunto residencial tiene datos con formato inválido.");
        register("request.payload.invalid.fields",
                "Los campos {fields} deben tener un formato válido (UUID si aplica).");
        register("request.payload.invalid.technical", "INVALID_CONJUNTO_REQUEST_PAYLOAD");
    }

    private MessageCatalog() {
        // Evitar instanciación
    }

    public static Message getMessageValue(String key) {
        return MESSAGES.get(key);
    }

    public static void synchronizeMessageValue(Message message) {
        register(message.getKey(), message.getValue());
    }

    public static Message removeMessage(String key) {
        return MESSAGES.remove(key);
    }

    public static Map<String, Message> getAllMessages() {
        return MESSAGES;
    }

    private static void register(final String key, final String value) {
        MESSAGES.put(key, new Message(key, value));
    }
}
