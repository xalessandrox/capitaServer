package com.sandro.utils;

import com.sandro.domain.UserPrincipal;
import com.sandro.dto.UserDTO;
import org.springframework.security.core.Authentication;

/**
 * @author Alessandro Formica
 * @version 1.0
 * @since 26.07.2023
 */


public class UserUtils {

    public static UserDTO getAuthenticatedUser(Authentication authentication) {
        return (UserDTO) authentication.getPrincipal();
    }

    public static UserDTO getLoggedInUser(Authentication authentication) {
        return ((UserPrincipal) authentication.getPrincipal()).getUser();
    }

}
