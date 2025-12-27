package ec.edu.ups.icc.fundamentos01.exception.domain;

import org.springframework.http.HttpStatus;
import ec.edu.ups.icc.fundamentos01.exception.base.ApplicationException;

/**
 * Excepción lanzada cuando un recurso solicitado no existe en el sistema.
 * 
 * Corresponde al código de estado HTTP 404 Not Found.
 * 
 * Cuándo usar:
 * - Al buscar una entidad por ID y no se encuentra
 * - Al intentar actualizar o eliminar un recurso inexistente
 * - En operaciones que requieren que el recurso exista previamente
 * 
 * Ejemplo:
 * 
 * <pre>
 * productRepository.findById(id)
 *         .orElseThrow(() -> new NotFoundException("Producto no encontrado con ID: " + id));
 * </pre>
 */
public class NotFoundException extends ApplicationException {

    /**
     * Constructor que acepta un mensaje descriptivo del recurso no encontrado.
     * 
     * @param message Mensaje explicando qué recurso no fue encontrado
     */
    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
