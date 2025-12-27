package ec.edu.ups.icc.fundamentos01.exception.base;

import org.springframework.http.HttpStatus;

/**
 * Excepción base abstracta para todas las excepciones personalizadas de la
 * aplicación.
 * 
 * Todas las excepciones de dominio deben extender esta clase y definir
 * su código de estado HTTP apropiado.
 */
public abstract class ApplicationException extends RuntimeException {

    private final HttpStatus status;

    /**
     * Constructor que requiere un estado HTTP y un mensaje.
     * 
     * @param status  El código de estado HTTP asociado a esta excepción
     * @param message El mensaje descriptivo del error
     */
    protected ApplicationException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    /**
     * Obtiene el código de estado HTTP asociado a esta excepción.
     * 
     * @return El HttpStatus de esta excepción
     */
    public HttpStatus getStatus() {
        return status;
    }
}
