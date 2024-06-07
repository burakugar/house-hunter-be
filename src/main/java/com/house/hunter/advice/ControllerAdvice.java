package com.house.hunter.advice;

import com.house.hunter.exception.DocumentAlreadyExistsException;
import com.house.hunter.exception.DocumentNotFoundException;
import com.house.hunter.exception.FileOperationException;
import com.house.hunter.exception.IllegalAccessRequestException;
import com.house.hunter.exception.IllegalRequestException;
import com.house.hunter.exception.ImageAlreadyExistsException;
import com.house.hunter.exception.ImageNotFoundException;
import com.house.hunter.exception.InvalidAccountStatusException;
import com.house.hunter.exception.InvalidDocumentTypeException;
import com.house.hunter.exception.InvalidTokenException;
import com.house.hunter.exception.InvalidUserAuthenticationException;
import com.house.hunter.exception.InvalidVerificationTokenException;
import com.house.hunter.exception.MailServiceException;
import com.house.hunter.exception.NoPropertyRequestFoundException;
import com.house.hunter.exception.PropertyAlreadyExistsException;
import com.house.hunter.exception.PropertyNotVerifiedException;
import com.house.hunter.exception.UserAlreadyExistsException;
import com.house.hunter.exception.UserNotFoundException;
import com.house.hunter.model.dto.error.ErrorDto;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.el.PropertyNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.UnexpectedTypeException;
import org.postgresql.util.PSQLException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@org.springframework.web.bind.annotation.ControllerAdvice
public final class ControllerAdvice {
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorDto> handleValidationException(ConstraintViolationException ex) {
        List<String> errorMessages = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());
        final ErrorDto error = new ErrorDto(HttpStatus.BAD_REQUEST.value(), "Validation failed", errorMessages);
        return ResponseEntity.status(error.getStatus()).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        List<String> errorMessages = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .collect(Collectors.toList());
        final ErrorDto error = new ErrorDto(HttpStatus.BAD_REQUEST.value(), "Validation failed", errorMessages);
        return ResponseEntity.status(error.getStatus()).body(error);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorDto> handleValidationException(UserAlreadyExistsException ex) {
        final ErrorDto error = new ErrorDto(HttpStatus.CONFLICT.value(), "User already exists", List.of(ex.getMessage()));
        return ResponseEntity.status(error.getStatus()).body(error);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorDto> handleValidationException(UserNotFoundException ex) {
        final ErrorDto error = new ErrorDto(HttpStatus.NOT_FOUND.value(), "User not found! ", List.of(ex.getMessage()));
        return ResponseEntity.status(error.getStatus()).body(error);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorDto> handleValidationException(InvalidTokenException ex) {
        final ErrorDto error = new ErrorDto(HttpStatus.UNAUTHORIZED.value(), ex.getMessage(), List.of(ex.getMessage()));
        return ResponseEntity.status(error.getStatus()).body(error);
    }

    @ExceptionHandler(InvalidUserAuthenticationException.class)
    public ResponseEntity<ErrorDto> handleValidationException(InvalidUserAuthenticationException ex) {
        final ErrorDto error = new ErrorDto(HttpStatus.UNAUTHORIZED.value(), ex.getMessage(), List.of(ex.getMessage()));
        return ResponseEntity.status(error.getStatus()).body(error);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorDto> handleValidationException(BadCredentialsException ex) {
        final ErrorDto error = new ErrorDto(HttpStatus.UNAUTHORIZED.value(), "Invalid username or password", List.of(ex.getMessage()));
        return ResponseEntity.status(error.getStatus()).body(error);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorDto> handleValidationException(ExpiredJwtException ex) {
        final ErrorDto error = new ErrorDto(HttpStatus.BAD_REQUEST.value(), "JWT is expired", List.of(ex.getMessage()));
        return ResponseEntity.status(error.getStatus()).body(error);
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<ErrorDto> handleValidationException(InternalAuthenticationServiceException ex) {
        final ErrorDto error = new ErrorDto(HttpStatus.UNAUTHORIZED.value(), ex.getMessage(), List.of(ex.getMessage()));
        return ResponseEntity.status(error.getStatus()).body(error);
    }

    @ExceptionHandler(IllegalRequestException.class)
    public ResponseEntity<ErrorDto> handleValidationException(IllegalRequestException ex) {
        final ErrorDto error = new ErrorDto(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), List.of(ex.getMessage()));
        return ResponseEntity.status(error.getStatus()).body(error);
    }

    @ExceptionHandler(PropertyNotFoundException.class)
    public ResponseEntity<ErrorDto> handleValidationException(PropertyNotFoundException ex) {
        final ErrorDto error = new ErrorDto(HttpStatus.NOT_FOUND.value(), ex.getMessage(), List.of(ex.getMessage()));
        return ResponseEntity.status(error.getStatus()).body(error);
    }

    @ExceptionHandler(ImageNotFoundException.class)
    public ResponseEntity<ErrorDto> handleValidationException(ImageNotFoundException ex) {
        final ErrorDto error = new ErrorDto(HttpStatus.NOT_FOUND.value(), ex.getMessage(), List.of(ex.getMessage()));
        return ResponseEntity.status(error.getStatus()).body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorDto> handleValidationException(HttpMessageNotReadableException ex) {
        final ErrorDto error = new ErrorDto(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), List.of(ex.getMessage()));
        return ResponseEntity.status(error.getStatus()).body(error);
    }

    @ExceptionHandler(ImageAlreadyExistsException.class)
    public ResponseEntity<ErrorDto> handleValidationException(ImageAlreadyExistsException ex) {
        final ErrorDto error = new ErrorDto(HttpStatus.CONFLICT.value(), ex.getMessage(), List.of(ex.getMessage()));
        return ResponseEntity.status(error.getStatus()).body(error);
    }

    @ExceptionHandler(DocumentAlreadyExistsException.class)
    public ResponseEntity<ErrorDto> handleValidationException(DocumentAlreadyExistsException ex) {
        final ErrorDto error = new ErrorDto(HttpStatus.CONFLICT.value(), ex.getMessage(), List.of(ex.getMessage()));
        return ResponseEntity.status(error.getStatus()).body(error);
    }

    @ExceptionHandler(DocumentNotFoundException.class)
    public ResponseEntity<ErrorDto> handleValidationException(DocumentNotFoundException ex) {
        final ErrorDto error = new ErrorDto(HttpStatus.NOT_FOUND.value(), ex.getMessage(), List.of(ex.getMessage()));
        return ResponseEntity.status(error.getStatus()).body(error);
    }

    @ExceptionHandler(InvalidDocumentTypeException.class)
    public ResponseEntity<ErrorDto> handleValidationException(InvalidDocumentTypeException ex) {
        final ErrorDto error = new ErrorDto(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), List.of(ex.getMessage()));
        return ResponseEntity.status(error.getStatus()).body(error);
    }

    @ExceptionHandler(FileOperationException.class)
    public ResponseEntity<ErrorDto> handleValidationException(FileOperationException ex) {
        final ErrorDto error = new ErrorDto(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), List.of(ex.getMessage()));
        return ResponseEntity.status(error.getStatus()).body(error);
    }

    @ExceptionHandler(IllegalAccessRequestException.class)
    public ResponseEntity<ErrorDto> handleValidationException(IllegalAccessRequestException ex) {
        final ErrorDto error = new ErrorDto(HttpStatus.FORBIDDEN.value(), ex.getMessage(), List.of(ex.getMessage()));
        return ResponseEntity.status(error.getStatus()).body(error);
    }

    @ExceptionHandler(PropertyAlreadyExistsException.class)
    public ResponseEntity<ErrorDto> handleValidationException(PropertyAlreadyExistsException ex) {
        final ErrorDto error = new ErrorDto(HttpStatus.CONFLICT.value(), ex.getMessage(), List.of(ex.getMessage()));
        return ResponseEntity.status(error.getStatus()).body(error);
    }


    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorDto> handleValidationException(DataIntegrityViolationException ex) {
        if (ex.getMessage().contains("violates unique constraint")) {
            final ErrorDto error = new ErrorDto(HttpStatus.BAD_REQUEST.value(), "The phone number is either invalid or taken", List.of("The phone number is either invalid or taken"));
            return ResponseEntity.status(error.getStatus()).body(error);
        }
        final ErrorDto error = new ErrorDto(HttpStatus.BAD_REQUEST.value(), ex.getCause().getMessage(), List.of(ex.getCause().getMessage()));
        return ResponseEntity.status(error.getStatus()).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDto> handleValidationException(IllegalArgumentException ex) {
        final ErrorDto error = new ErrorDto(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), List.of(ex.getMessage()));
        return ResponseEntity.status(error.getStatus()).body(error);
    }

    @ExceptionHandler(InvalidAccountStatusException.class)
    public ResponseEntity<ErrorDto> handleValidationException(InvalidAccountStatusException ex) {
        final ErrorDto error = new ErrorDto(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), List.of(ex.getMessage()));
        return ResponseEntity.status(error.getStatus()).body(error);
    }

    @ExceptionHandler(InvalidVerificationTokenException.class)
    public ResponseEntity<ErrorDto> handleValidationException(InvalidVerificationTokenException ex) {
        final ErrorDto error = new ErrorDto(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), List.of(ex.getMessage()));
        return ResponseEntity.status(error.getStatus()).body(error);
    }

    @ExceptionHandler(MailServiceException.class)
    public ResponseEntity<ErrorDto> handleValidationException(MailServiceException ex) {
        final ErrorDto error = new ErrorDto(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), List.of(ex.getMessage()));
        return ResponseEntity.status(error.getStatus()).body(error);
    }

    @ExceptionHandler(UnexpectedTypeException.class)
    public ResponseEntity<ErrorDto> handleValidationException(UnexpectedTypeException ex) {
        final ErrorDto error = new ErrorDto(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), List.of(ex.getMessage()));
        return ResponseEntity.status(error.getStatus()).body(error);
    }

    @ExceptionHandler(PSQLException.class)
    public ResponseEntity<ErrorDto> handleValidationException(PSQLException ex) {
        final ErrorDto error = new ErrorDto(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), List.of(ex.getMessage()));
        return ResponseEntity.status(error.getStatus()).body(error);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorDto> handleValidationException(IllegalStateException ex) {
        final ErrorDto error = new ErrorDto(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), List.of(ex.getMessage()));
        return ResponseEntity.status(error.getStatus()).body(error);
    }

    @ExceptionHandler(NoPropertyRequestFoundException.class)
    public ResponseEntity<ErrorDto> handleValidationException(NoPropertyRequestFoundException ex) {
        final ErrorDto error = new ErrorDto(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), List.of(ex.getMessage()));
        return ResponseEntity.status(error.getStatus()).body(error);
    }

    @ExceptionHandler(PropertyNotVerifiedException.class)
    public ResponseEntity<ErrorDto> handleValidationException(PropertyNotVerifiedException ex) {
        final ErrorDto error = new ErrorDto(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), List.of(ex.getMessage()));
        return ResponseEntity.status(error.getStatus()).body(error);
    }

}
