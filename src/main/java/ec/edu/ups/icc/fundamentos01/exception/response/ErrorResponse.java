package ec.edu.ups.icc.fundamentos01.exception.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Clase de respuesta estandarizada para todos los errores de la API.
 * 
 * Proporciona un formato consistente para comunicar errores al cliente,
 * incluyendo detalles opcionales para errores de validación de campos.
 * 
 * El campo 'details' solo aparece en la respuesta JSON cuando contiene valores,
 * gracias a la anotación @JsonInclude(NON_NULL).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse implements Serializable {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private Map<String, String> details;

    /**
     * Constructor completo para errores con detalles de validación por campo.
     * 
     * Usado cuando hay múltiples errores de validación y se necesita especificar
     * qué campos fallaron y por qué.
     * 
     * @param status  El código de estado HTTP
     * @param message Mensaje general del error
     * @param path    La ruta URI de la solicitud
     * @param details Mapa de campo -> mensaje de error
     */
    public ErrorResponse(
            HttpStatus status,
            String message,
            String path,
            Map<String, String> details) {
        this.timestamp = LocalDateTime.now();
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.message = message;
        this.path = path;
        this.details = details;
    }

    /**
     * Constructor simplificado para errores sin detalles de campos.
     * 
     * Usado para errores de dominio simples como recurso no encontrado,
     * conflictos o excepciones generales.
     * 
     * @param status  El código de estado HTTP
     * @param message Mensaje del error
     * @param path    La ruta URI de la solicitud
     */
    public ErrorResponse(HttpStatus status, String message, String path) {
        this(status, message, path, null);
    }

    // Getters

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getDetails() {
        return details;
    }
}
