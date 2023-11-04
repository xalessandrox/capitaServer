package com.sandro.service;

import com.sandro.enumeration.VerificationType;

/**
 * @author Alessandro Formica
 * @version 1.0
 * @since 31.08.2023
 */


public interface EmailService {

    void sendVerificationEmail(String firstName, String email, String verificationUrl, VerificationType verificationType);

}
