package com.sandro.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * @author Alessandro Formica
 * @version 1.0
 * @since 27.07.2023
 */

@Getter
@Setter
@RequiredArgsConstructor
public class UpdateForm {

    private Long id;

    @NotNull(message = "ID cannot be null")
    private String firstName;

    @NotEmpty(message = "Last name cannot be empty")
    private String lastName;

    @NotEmpty(message = "Email cannot be empty")
    @Email(message = "Please enter a valid email")
    private String email;

    @Pattern(regexp = "^\\d{11}$", message = "Invalid phone number")
    private String phone;

    private String address;
    private String title;
    private String bio;



    @Override
    public String toString() {
        return "UpdateForm {" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
