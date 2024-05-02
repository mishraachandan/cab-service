package com.assignment.cabservice.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@RestControllerAdvice
public class CustomizedResponseEntityHandler{

//    @ExceptionHandler(Exception.class)
//    public final ResponseEntity<ErrorDetails> handleAllException(Exception ex, WebRequest request) {
//        ErrorDetails errorDetails=new ErrorDetails(LocalDateTime.now(),
//                ex.getMessage(),request.getDescription(false));
//        return new ResponseEntity<ErrorDetails>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
//    }

//    @ExceptionHandler(CarNotFoundException.class)
//    public final ResponseEntity<ErrorDetails> handleCarNotFoundException(Exception ex, WebRequest request) {
//        ErrorDetails errorDetails=new ErrorDetails(LocalDateTime.now(),
//                ex.getMessage(),request.getDescription(false));
//        return new ResponseEntity<ErrorDetails>(errorDetails, HttpStatus.BAD_REQUEST);
//    }

    @ExceptionHandler(CarNotFoundException.class)
    public final ResponseEntity<ErrorDetails> handleCarNotFoundException(CarNotFoundException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public final ResponseEntity<ErrorDetails> handleArgumentMismatchException(MethodArgumentTypeMismatchException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(),
                "You have passed wrong number of parameters in the url request.", request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_GATEWAY);
    }


//    @ExceptionHandler(InvalidSeatingCapacityException.class)
//    public final ResponseEntity<ErrorDetails> handleUserNotFoundException(Exception ex, WebRequest request) {
//        ErrorDetails errorDetails=new ErrorDetails(LocalDateTime.now(),
//                ex.getMessage(),request.getDescription(false));
//        return new ResponseEntity<ErrorDetails>(errorDetails, HttpStatus.NOT_FOUND);
//    }

//    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
//                                                                  HttpHeaders headers, HttpStatus status,
//                                                                  WebRequest request) {
//        ErrorDetails errorDetails=new ErrorDetails(LocalDateTime.now(),
//                ex.getMessage(),request.getDescription(false));
//        return new ResponseEntity(errorDetails, HttpStatus.BAD_REQUEST);
//    }
}
