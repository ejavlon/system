package uz.uychiitschool.system.web.base.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import uz.uychiitschool.system.web.base.dto.ResponseApi;

@ControllerAdvice
public class GlobalExceptionHandler {

    // 1. DataNotFoundException uchun exception handler
    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<ResponseApi<Void>> handleDataNotFoundException(DataNotFoundException ex, WebRequest request) {
        ResponseApi<Void> response = ResponseApi.<Void>builder()
                .success(false)
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // 2. DuplicateEntityException uchun exception handler
    @ExceptionHandler(DuplicateEntityException.class)
    public ResponseEntity<ResponseApi<Void>> handleDuplicateEntityException(DuplicateEntityException ex, WebRequest request) {
        ResponseApi<Void> response = ResponseApi.<Void>builder()
                .success(false)
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    // 3. RuntimeException (umumiy exception) uchun exception handler
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseApi<Void>> handleRuntimeException(RuntimeException ex, WebRequest request) {
        ResponseApi<Void> response = ResponseApi.<Void>builder()
                .success(false)
                .message("Internal Server Error: " + ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 4. Boshqa barcha exceptionlar uchun umumiy exception handler
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseApi<Void>> handleGlobalException(Exception ex, WebRequest request) {
        ResponseApi<Void> response = ResponseApi.<Void>builder()
                .success(false)
                .message("Unexpected Error: " + ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

