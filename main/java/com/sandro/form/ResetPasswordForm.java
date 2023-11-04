package com.sandro.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Alessandro Formica
 * @version 1.0
 * @since 30.08.2023
 */

@Getter
@Setter
public class ResetPasswordForm {
    @NotNull(message = "ID cannot be null or empty")
    private Long userId;
    @NotEmpty(message = "Password cannot be empty")
    private String newPassword;
    @NotEmpty(message = "Password must be confirmed")
    private String confirmNewPassword;
}
