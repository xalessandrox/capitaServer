package com.sandro.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;

import java.util.Map;

/**
 * @author Alessandro Formica
 * @version 1.0
 * @since 05.06.2023
 */


@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@SuperBuilder
public class HttpResponse {

    protected String timeStamp;
    protected int statusCode;
    protected HttpStatus httpStatus;
    protected String reason;
    protected String message;
    protected String developerMessage;
    protected Map<?, ?> data;

}
