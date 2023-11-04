package com.sandro;

import com.sandro.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.verification.VerificationMode;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class SecurecapitaApplicationTests {

    @MockBean
    User user;

    @Test
    void contextLoads() {
        VerificationMode mode =  Mockito.atMost(3);
        Mockito.when(user.getEmail()).thenReturn("formicale@hotmail.com");

    }

}
