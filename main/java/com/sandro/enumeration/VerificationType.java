package com.sandro.enumeration;

/**
 * @author Alessandro Formica
 * @version 1.0
 * @since 05.06.2023
 */

public enum VerificationType {
    ACCOUNT("ACCOUNT"),
    PASSWORD("PASSWORD");

    private final String type;
    VerificationType(String type) {
        this.type = type;
    }

    public String getType() {
        return type.toLowerCase();
    }
}
