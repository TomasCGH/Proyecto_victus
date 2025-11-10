package co.edu.uco.messageservice.catalog;

/**
 * Representa un mensaje parametrizado para los procesos de registro y
 * gestión de viviendas. Cada mensaje cuenta con una clave única utilizada por
 * los diferentes componentes del ecosistema para obtener textos de negocio.
 */
public class Message {

    private String key;
    private String value;

    public Message() {
        // Constructor por defecto necesario para la deserialización reactiva.
    }

    public Message(String key, String value) {
        setKey(key);
        setValue(value);
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
}
