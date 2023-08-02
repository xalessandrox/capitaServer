package com.sandro.exception;

/**
 * @author Alessandro Formica
 * @version 1.0
 * @since 04.06.2023
 */


public class ApiException extends RuntimeException {
    public ApiException(String message) {
        super(message);
    }

}
