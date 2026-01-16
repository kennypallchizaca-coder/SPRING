package ec.edu.ups.icc.fundamentos01.exception.handler;

import ec.edu.ups.icc.fundamentos01.exception.base.ApplicationException;
import ec.edu.ups.icc.fundamentos01.exception.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para toda la aplicación.
 * 
 * Esta clase intercepta las excepciones lanzadas por los controladores y
 * servicios,
 * y las convierte en respuestas HTTP consistentes y bien formateadas.
 * 
 * Es el único punto de manejo de errores, eliminando la necesidad de try/catch
 * en controladores y servicios.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja todas las excepciones personalizadas que extienden
     * ApplicationException.
     * 
     * Incluye: NotFoundException, ConflictException, BadRequestException, etc.
     * 
     * @param ex      La excepción de aplicación lanzada
     * @param request La solicitud HTTP que causó el error
     * @return ResponseEntity con ErrorResponse y el código de estado apropiado
     */
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponse> handleApplicationException(
            ApplicationException ex,
            HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(
                ex.getStatus(),
                ex.getMessage(),
                request.getRequestURI());

        return ResponseEntity
                .status(ex.getStatus())
                .body(response);
    }

    /**
     * Maneja errores de validación de Bean Validation (@Valid).
     * 
     * Extrae todos los errores de validación de campos y los incluye en
     * un mapa 'details' para que el cliente sepa exactamente qué corregir.
     * 
     * @param ex      La excepción de validación
     * @param request La solicitud HTTP que causó el error
     * @return ResponseEntity con ErrorResponse incluyendo details por campo
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();

        // Extraer cada error de campo
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Datos de entrada inválidos",
                request.getRequestURI(),
                errors);

        return ResponseEntity
                .badRequest()
                .body(response);
    }

    /**
     * Maneja cualquier excepción no esperada o no manejada específicamente.
     * 
     * Sirve como red de seguridad para evitar exponer detalles internos
     * al cliente en caso de errores inesperados.
     * 
     * @param ex      La excepción genérica
     * @param request La solicitud HTTP que causó el error
     * @return ResponseEntity con ErrorResponse genérico de error interno
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(
            Exception ex,
            HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error interno del servidor",
                request.getRequestURI());


        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}
