package cz.marekvele.personal.morosystemsdemo.rest.errorhandling;

import cz.marekvele.personal.morosystemsdemo.model.ApiError;
import cz.marekvele.personal.morosystemsdemo.rest.exception.CommonException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
@RequiredArgsConstructor
public class ApiExceptionHandler {

    @Autowired
    private final MessageSource messageSource;

    @ExceptionHandler(CommonException.class)
    public ResponseEntity<ApiError> handleBusinessException(CommonException exception, HttpServletRequest request) {
        String localizedMessage = messageSource.getMessage(exception.getClass().getCanonicalName(), exception.getArgs(), request.getLocale());
        return error(exception.getStatus(), localizedMessage, request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleMismatchedPathParameter(
            MethodArgumentTypeMismatchException exception,
            HttpServletRequest request
    ) {
        String message = String.format("Path parameter '%s' must be a valid number", exception.getName());
        return error(HttpStatus.BAD_REQUEST, message, request);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiError> handleInvalidPathParameter(
            HandlerMethodValidationException exception,
            HttpServletRequest request
    ) {
        List<String> messages = exception.getParameterValidationResults().stream()
                .flatMap(result -> result.getResolvableErrors().stream()
                        .map(error ->
                                String.format("Parameter '%s' %s", result.getMethodParameter().getParameterName(),
                                        error.getDefaultMessage())))
                .toList();

        return error(HttpStatus.BAD_REQUEST, messages, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleInvalidPathParameter(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        List<String> messages = exception.getFieldErrors().stream()
                .map(error ->
                    String.format("Field '%s'  %s", error.getField(),
                            error.getDefaultMessage()))
                .toList();

        return error(HttpStatus.BAD_REQUEST, messages, request);
    }

    private ResponseEntity<ApiError> error(
            HttpStatus status,
            String message,
            HttpServletRequest request
    ) {
        return error(status,List.of(message), request);
    }

    private ResponseEntity<ApiError> error(
            HttpStatus status,
            List<String> message,
            HttpServletRequest request
    ) {
        ApiError body = new ApiError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(body);
    }
}
