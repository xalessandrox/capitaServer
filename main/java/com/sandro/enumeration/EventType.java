package com.sandro.enumeration;

import lombok.Getter;

/**
 * @author Alessandro Formica
 * @version 1.0
 * @since 04.08.2023
 */

@Getter
public enum EventType {

    LOGIN_ATTEMPT("You tried to login"),
    LOGIN_ATTEMPT_FAILURE("Failed attempt to login"),
    LOGIN_ATTEMPT_SUCCESS("Successful attempt to login"),
    PROFILE_UPDATE("Profile informations updated"),
    PROFILE_PICTURE_UPDATE("Profile picture updated"),
    ROLE_UPDATE("Role and permissions updated"),
    ACCOUNT_SETTINGS_UPDATE("Account setting updated"),
    MFA_UPDATE("Multi-Factor Authentication updated"),
    PASSWORD_UPDATE("Password updated");

    private final String description;
    EventType(String description) {
        this.description = description;
    }


}
