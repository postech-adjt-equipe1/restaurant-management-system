package br.com.fiap.restaurante.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static ProblemDetail problem(HttpStatus status, String title, String detail) {
        ProblemDetail pd = ProblemDetail.forStatus(status);
        pd.setType(URI.create("https://fiap.com.br/erros/" + status.value()));
        pd.setTitle(title);
        pd.setDetail(detail);
        return pd;
    }

    private static ProblemDetail problem(HttpStatus status, String detail) {
        return problem(status, status.getReasonPhrase(), detail);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> campos = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (m1, m2) -> m1,
                        LinkedHashMap::new
                ));
        ProblemDetail pd = problem(HttpStatus.BAD_REQUEST, "Erro de validação",
                "Um ou mais campos possuem valores inválidos. Consulte o campo 'campos' para detalhes.");
        pd.setProperty("campos", campos);
        return ResponseEntity.badRequest().body(pd);
    }

    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<ProblemDetail> handleMissingPathVariable(MissingPathVariableException ex) {
        return ResponseEntity.badRequest()
                .body(problem(HttpStatus.BAD_REQUEST, "Parâmetro obrigatório ausente",
                        "O parâmetro de rota '" + ex.getVariableName() + "' é obrigatório e não foi informado."));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.badRequest()
                .body(problem(HttpStatus.BAD_REQUEST, "Tipo de parâmetro inválido",
                        "O parâmetro '" + ex.getName() + "' recebeu um valor inválido: '" + ex.getValue() + "'."));
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ProblemDetail> handleInvalidPassword(InvalidPasswordException ex) {
        return ResponseEntity.badRequest().body(problem(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ProblemDetail> handleInvalidCredentials(InvalidCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(problem(HttpStatus.UNAUTHORIZED, ex.getMessage()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(problem(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler({DuplicateEmailException.class, DuplicateLoginException.class})
    public ResponseEntity<ProblemDetail> handleDuplicate(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(problem(HttpStatus.CONFLICT, ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(problem(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Ocorreu um erro interno. Tente novamente mais tarde."));
    }
}
