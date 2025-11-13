package co.edu.uco.messageservice.catalog;

/**
 * Representa un mensaje parametrizado para los procesos de registro y
 * gestión de viviendas. Cada mensaje cuenta con una clave única utilizada por
 * los diferentes componentes del ecosistema para obtener textos de negocio.
 */
public class Message {

    private String key;
    private String value;
    private String technicalMessage;
    private String clientMessage;

    public Message() {
        // Constructor por defecto necesario para la deserialización reactiva.
    }

    public Message(String key, String value) {
        setKey(key);
        setValue(value);
        // Backward compatibility: store value as clientMessage
        setClientMessage(value);
    }

    public Message(String key, String technicalMessage, String clientMessage) {
        setKey(key);
        setTechnicalMessage(technicalMessage);
        setClientMessage(clientMessage);
        setValue(clientMessage);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTechnicalMessage() {
        return technicalMessage;
    }

    public void setTechnicalMessage(String technicalMessage) {
        this.technicalMessage = technicalMessage;
    }

    public String getClientMessage() {
        return clientMessage;
    }

    public void setClientMessage(String clientMessage) {
        this.clientMessage = clientMessage;
    }
}
