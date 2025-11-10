package co.edu.uco.messageservice.catalog;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Catálogo en memoria que expone mensajes de negocio relacionados con el ciclo
 * de vida de las viviendas. Los mensajes son consumidos por otros microservicios
 * para mantener consistencia textual en notificaciones y respuestas HTTP.
 */
public final class MessageCatalog {

    private static final Map<String, Message> MESSAGES = new ConcurrentHashMap<>();

    static {
        register("exception.general.unexpected",
                "Ha ocurrido un error inesperado al gestionar la vivienda. Intente nuevamente más tarde.");
        register("exception.general.technical",
                "Se produjo un error interno al procesar la operación de vivienda.");
        register("exception.general.request",
                "No fue posible procesar la solicitud de vivienda con la información recibida.");

        register("register.vivienda.success", "Vivienda registrada exitosamente.");
        register("register.vivienda.validation.numero.required", "El número de la vivienda es obligatorio.");
        register("register.vivienda.validation.numero.length", "El número de la vivienda debe tener máximo 10 caracteres.");
        register("register.vivienda.validation.numero.invalidFormat",
                "El número de la vivienda solo puede contener letras, números y guiones.");
        register("register.vivienda.validation.tipo.required", "El tipo de vivienda es obligatorio.");
        register("register.vivienda.validation.tipo.notFound", "El tipo de vivienda indicado no está configurado.");
        register("register.vivienda.validation.estado.required", "El estado de la vivienda es obligatorio.");
        register("register.vivienda.validation.estado.invalid",
                "El estado de la vivienda no es válido para el registro.");
        register("register.vivienda.validation.conjunto.required", "Debe asignarse un conjunto residencial válido.");
        register("register.vivienda.validation.conjunto.nombre.length",
                "El nombre del conjunto residencial no puede superar 80 caracteres.");

        register("list.viviendas.success", "Viviendas obtenidas exitosamente.");
        register("get.vivienda.success", "Vivienda obtenida exitosamente.");
        register("search.viviendas.success", "Viviendas filtradas exitosamente.");
        register("delete.vivienda.success", "Vivienda eliminada exitosamente.");
        register("list.viviendas.validation.page.negative", "La página solicitada no puede ser negativa.");
        register("list.viviendas.validation.size.invalid", "El tamaño de página debe estar entre 1 y 50 registros.");

        register("domain.vivienda.numero.duplicated", "Ya existe una vivienda registrada con ese número.");
        register("domain.vivienda.estado.invalid", "El estado de la vivienda no es válido.");
        register("domain.vivienda.create.success", "La vivienda fue creada correctamente.");
        register("domain.vivienda.update.success", "La información de la vivienda fue actualizada correctamente.");
        register("domain.vivienda.delete.success", "La vivienda fue eliminada correctamente.");
        register("domain.vivienda.changeEstado.success", "El estado de la vivienda se actualizó correctamente.");

        register("notification.vivienda.reserva.creada",
                "Se creó la reserva para la vivienda {numero}. Mantenga informada a la administración.");
        register("notification.vivienda.reserva.expirada",
                "La reserva de la vivienda {numero} ha expirado por falta de confirmación.");
        register("notification.vivienda.inspeccion.programada",
                "Se programó una inspección para la vivienda {numero} en el conjunto {conjunto}.");

        register("application.unexpectedError.user",
                "Se presentó un error inesperado al consultar los mensajes de vivienda. Intente nuevamente.");
        register("application.unexpectedError.technical", "UNEXPECTED_VIVIENDA_ERROR - revise trazas y causa raíz.");
        register("infrastructure.messageService.unavailable.user",
                "El servicio de mensajes de vivienda no está disponible en este momento.");
        register("infrastructure.messageService.unavailable.technical", "VIVIENDA_MESSAGE_SERVICE_UNAVAILABLE");
        register("infrastructure.parameterService.unavailable.user",
                "El servicio de parámetros de vivienda no está disponible.");
        register("infrastructure.parameterService.unavailable.technical", "VIVIENDA_PARAMETER_SERVICE_UNAVAILABLE");
        register("infrastructure.parameterService.invalidResponse.user",
                "Se recibió un valor de parámetro de vivienda inválido.");
        register("infrastructure.parameterService.invalidResponse.technical", "VIVIENDA_PARAMETER_INVALID_RESPONSE");

        register("request.payload.invalid", "El cuerpo de la solicitud de vivienda tiene datos con formato inválido.");
        register("request.payload.invalid.fields",
                "Los campos {fields} deben tener un formato válido (UUID si aplica).");
        register("request.payload.invalid.technical", "INVALID_VIVIENDA_REQUEST_PAYLOAD");
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
