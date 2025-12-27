package ec.edu.ups.icc.fundamentos01.exception.domain;

import org.springframework.http.HttpStatus;
import ec.edu.ups.icc.fundamentos01.exception.base.ApplicationException;

/**
 * Excepción lanzada cuando la solicitud del cliente no puede ser procesada
 * debido a datos inválidos o que violan reglas de negocio.
 * 
 * Corresponde al código de estado HTTP 400 Bad Request.
 * 
 * Cuándo usar:
 * - Cuando los datos son válidos técnicamente pero violan reglas de negocio
 * - Al detectar operaciones no permitidas según el estado del sistema
 * - Para condiciones de dominio no cumplidas (stock insuficiente, saldo
 * negativo)
 * - Validaciones específicas no cubiertas por Bean Validation
 * 
 * Ejemplo:
 * 
 * <pre>
 * if (product.getStock() < quantity) {
 *     throw new BadRequestException(
 *             "Stock insuficiente. Disponible: " + product.getStock() +
 *                     ", solicitado: " + quantity);
 * }
 * </pre>
 */
public class BadRequestException extends ApplicationException {

    /**
     * Constructor que acepta un mensaje descriptivo del error de validación.
     * 
     * @param message Mensaje explicando por qué la solicitud es inválida
     */
    public BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
