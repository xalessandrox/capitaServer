package com.sandro.utils;

import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sandro.domain.HttpResponse;
import com.sandro.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;

/**
 * @author Alessandro Formica
 * @version 1.0
 * @since 05.07.2023
 */


@Slf4j
public class ExceptionUtils {

    private static final String GENERIC_EXCEPTION_MESSAGE = "An error occurred. Please try later";

    public static void processError(HttpServletRequest request, HttpServletResponse response, Exception exception) {
        HttpResponse httpResponse = null;
        if (exception instanceof ApiException
                || exception instanceof DisabledException
                || exception instanceof LockedException
                || exception instanceof InvalidClaimException
                || exception instanceof BadCredentialsException) {

            httpResponse = getHttpResponse(response, exception.getMessage(), HttpStatus.BAD_REQUEST);

        } else if (exception instanceof TokenExpiredException) {

            httpResponse = getHttpResponse(response, exception.getMessage(), HttpStatus.UNAUTHORIZED);

        } else {

            httpResponse = getHttpResponse(response, GENERIC_EXCEPTION_MESSAGE, HttpStatus.BAD_REQUEST);

        }

        writeResponse(response, httpResponse);
        log.error("Error: {}", exception.getMessage());
    }

    private static void writeResponse(HttpServletResponse response, HttpResponse httpResponse) {
        OutputStream out;
        try {
            out = response.getOutputStream();
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(out, httpResponse);
            out.flush();
        } catch (IOException exception) {
            log.error("IO Exception: {}", exception.getMessage());
            exception.printStackTrace();
        } catch (Exception exception) {
            log.error("An Exception occurred: {}", exception.getMessage());
            exception.printStackTrace();
        }
    }

    private static HttpResponse getHttpResponse(HttpServletResponse response, String message, HttpStatus httpStatus) {
        HttpResponse httpResponse = HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .reason(message)
                .httpStatus(httpStatus)
                .statusCode(httpStatus.value())
                .build();
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        return httpResponse;
    }

}
