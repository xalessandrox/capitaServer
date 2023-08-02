package com.sandro.form;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Alessandro Formica
 * @version 1.0
 * @since 31.07.2023
 */

@Getter
@Setter
public class UpdatePasswordForm {
    @NotNull(message = "Current password cannot be empty")
    private String currentPassword;
    @NotNull(message = "New password cannot be empty")
    private String newPassword;
    @NotNull(message = "Confirm new password cannot be empty")
    private String confirmNewPassword;

}
