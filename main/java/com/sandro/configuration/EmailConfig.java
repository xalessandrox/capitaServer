package com.sandro.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * @author Alessandro Formica
 * @version 1.0
 * @since 17.09.2023
 */

@Configuration
public class EmailConfig {

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp-mail.outlook.com");
        mailSender.setPort(587);
        mailSender.setUsername("formicale@hotmail.com");
        mailSender.setPassword("umSs_77_wiFa_77_!");
        mailSender.setJavaMailProperties(getEmailProps());
        return mailSender;

    }

    Properties getEmailProps() {
        Properties props = new Properties();
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.smtp.starttls.enable", "true");
        props.setProperty("mail.smtp.host", "smtp-mail.outlook.com");
        props.setProperty("mail.smtp.port", "587");
//        props.setProperty("mail.smtp.ssl.trust", "false");
        return props;
    }

}
