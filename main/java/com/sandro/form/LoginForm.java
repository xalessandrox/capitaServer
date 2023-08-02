package com.sandro.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Alessandro Formica
 * @version 1.0
 * @since 01.07.2023
 */

@Getter
@Setter
public class LoginForm {
    @NotEmpty(message = "Email cannot be empty")
    @Email(message = "Please enter a valid email")
    private String email;
    @NotEmpty(message = "Password cannot be empty")
    private String password;
}
