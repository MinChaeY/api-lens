package com.apilens.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.apilens.user.exception.DuplicateEmailException;
import com.apilens.user.exception.InvalidCredentialsException;
import com.apilens.user.exception.UserNotFoundException;
import com.apilens.project.exception.ProjectNotFoundException;
import com.apilens.endpoint.exception.OpenApiImportException;
import com.apilens.endpoint.exception.OpenApiUrlNotRegisteredException;
import com.apilens.endpoint.exception.EndpointNotFoundException;
import com.apilens.testcase.exception.TestCaseNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEmail(
            DuplicateEmailException exception
    ) {
        ErrorResponse response = new ErrorResponse(
                "DUPLICATE_EMAIL",
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException exception
    ) {
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("잘못된 요청입니다.");

        ErrorResponse response = new ErrorResponse(
                "INVALID_INPUT",
                message
        );

        return ResponseEntity
                .badRequest()
                .body(response);
    }
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(
        InvalidCredentialsException exception
    ) {
        ErrorResponse response = new ErrorResponse(
                "INVALID_CREDENTIALS",
                exception.getMessage()
        );
        
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(response);
        }
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(
        UserNotFoundException exception
    ) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(
                    "USER_NOT_FOUND",
                    exception.getMessage()
            ));
        }
    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProjectNotFound(
        ProjectNotFoundException exception
    ) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(
                    "PROJECT_NOT_FOUND",
                    exception.getMessage()
            ));
        }

    @ExceptionHandler(OpenApiUrlNotRegisteredException.class)
    public ResponseEntity<ErrorResponse> handleOpenApiUrlNotRegistered( 
        OpenApiUrlNotRegisteredException exception
    ) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(
                    "OPENAPI_URL_NOT_REGISTERED",
                    exception.getMessage()
            ));
        }

    @ExceptionHandler(OpenApiImportException.class)
    public ResponseEntity<ErrorResponse> handleOpenApiImport(
        OpenApiImportException exception
    ) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(
                    "OPENAPI_IMPORT_FAILED",
                    exception.getMessage()
            ));
        }
    
    @ExceptionHandler(EndpointNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEndpointNotFound(
        EndpointNotFoundException exception
    ) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(
                    "ENDPOINT_NOT_FOUND",
                    exception.getMessage()
            ));
        }

    @ExceptionHandler(TestCaseNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTestCaseNotFound(
        TestCaseNotFoundException exception
    ) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(
                    "TEST_CASE_NOT_FOUND",
                    exception.getMessage()
            ));
        }
}