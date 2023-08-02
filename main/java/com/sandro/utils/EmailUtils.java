package com.sandro.utils;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.util.Properties;

/**
 * @author Alessandro Formica
 * @version 1.0
 * @since 02.07.2023
 */


public class EmailUtils {

    Properties getEmailProps() {
        Properties props = new Properties();
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.smtp.starttls.enable", "true");
        props.setProperty("mail.smtp.host", "smtp-mail.outlook.com");
        props.setProperty("mail.smtp.port", "587");
//        props.setProperty("mail.smtp.ssl.trust", "true");
        return props;
    }

    Session session = Session.getInstance(getEmailProps(), new Authenticator() {
                final String username = "formicale@hotmail.com";
                final String password = "umSs_77_wiFa_77_!";

                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            }
    );

    public void sendEmail(String recipient, String mailSubject, String mailContent) throws MessagingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("formicale@hotmail.com"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
        message.setSubject("Verification code");

        String msg = mailContent;

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(msg, "text/html; charset=utf-8");

        MimeMultipart mimeMultipart = new MimeMultipart();
        mimeMultipart.addBodyPart(mimeBodyPart);

        message.setContent(mimeMultipart);

        Transport.send(message);

    }


}
