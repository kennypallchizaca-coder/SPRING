package ec.edu.ups.icc.fundamentos01.exception.domain;

import org.springframework.http.HttpStatus;
import ec.edu.ups.icc.fundamentos01.exception.base.ApplicationException;

/**
 * Excepción lanzada cuando existe un conflicto con el estado actual del
 * recurso.
 * 
 * Corresponde al código de estado HTTP 409 Conflict.
 * 
 * Cuándo usar:
 * - Al intentar crear un recurso con identificador único ya existente
 * - Cuando se detecta duplicación de datos (email, username, código)
 * - Al violar restricciones de unicidad o integridad
 * - En conflictos de concurrencia o versiones
 * 
 * Ejemplo:
 * 
 * <pre>
 * if (userRepository.existsByEmail(email)) {
 *     throw new ConflictException("El email " + email + " ya está registrado");
 * }
 * </pre>
 */
public class ConflictException extends ApplicationException {

    /**
     * Constructor que acepta un mensaje descriptivo del conflicto.
     * 
     * @param message Mensaje explicando el conflicto de estado
     */
    public ConflictException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
