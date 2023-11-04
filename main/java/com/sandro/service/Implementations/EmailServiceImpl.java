package com.sandro.service.Implementations;

import com.sandro.enumeration.VerificationType;
import com.sandro.exception.ApiException;
import com.sandro.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * @author Alessandro Formica
 * @version 1.0
 * @since 31.08.2023
 */

@Service
@Slf4j
@AllArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendVerificationEmail(String firstName, String email, String verificationUrl, VerificationType verificationType) {

        CompletableFuture.runAsync(() -> {
            try {
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);

                messageHelper.setFrom("formicale@hotmail.com");
                messageHelper.setTo(email);
                messageHelper.setText(getEmailMessage(firstName, verificationUrl, verificationType), true);
                messageHelper.setSubject(String.format("Secure-Capita - %s Verification Email", StringUtils.capitalize(verificationType.getType())));
                mailSender.send(mimeMessage);
                log.info("Email sent to {}", firstName);
            } catch (Exception exception) {
                log.error(exception.getMessage());
            }
        });


    }

    private String getEmailMessage(String firstName, String verificationUrl, VerificationType verificationType) {

        switch (verificationType) {
            case PASSWORD -> {
                return mailTextOfTypePassword(firstName, verificationUrl);
            }
            case ACCOUNT -> {
                return mailTextOfTypeAccount(firstName, verificationUrl);
            }
            default -> {
                throw new ApiException("Unable to send email. ");
            }
        }

    }

    private String mailTextOfTypePassword(String firstName, String verificationUrl) {
        StringBuilder sb = new StringBuilder();
        sb
                .append("<html style='min-width:500px;'>" +
                        "<body style=' background-color: green; display: flex; flex-direction:column; justify-content:center; align-items:center; min-width:100%;'>" +
                        "<head><style> body {font-family:'Verdana';} </style></head>" +
                        "<h2 id='test'>You can now set a new password</h2>")
                .append("<a style='text-decoration:none; background:#222; color:silver; border-radius:5px;margin:10px;' href='").append(verificationUrl).append("'>")
                .append("<h3 style='padding:10px;margin-bottom: 5px;'>Click to safely reset the password</h3>")
                .append("</a>")
                .append("</body></html>");
        return sb.toString();

    }

    private String mailTextOfTypeAccount(String firstName, String verificationUrl) {
        StringBuilder sb = new StringBuilder();
        sb
                .append("<html style='min-width:500px;'>" +
                        "<body style=' background-color: green; display: flex; flex-direction:column; justify-content:center; align-items:center; min-width:100%;'>" +
                        "<head><style> body {font-family:'Verdana';} </style></head>" +
                        "<h2 id='test'>Your account was created</h2>")
                .append("<a style='text-decoration:none; background:#222; color:silver; border-radius:5px;margin:10px;' href='").append(verificationUrl).append("'>")
                .append("<h3 style='padding:10px;margin-bottom: 5px;'>Click to activate it and start navigate</h3>")
                .append("</a>")
                .append("</body></html>");
        return sb.toString();
    }


}
