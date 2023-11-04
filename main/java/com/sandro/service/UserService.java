package com.sandro.service;

import com.sandro.domain.User;
import com.sandro.dto.UserDTO;
import com.sandro.form.UpdateForm;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Alessandro Formica
 * @version 1.0
 * @since 05.06.2023
 */


public interface UserService {

    UserDTO createUser(User user);
    UserDTO getUserByEmail(String email);
    void sendVerificationCode(UserDTO userDTO);
    UserDTO verifyCode(String email, String verificationCode);
    void resetPassword(String email);
    UserDTO verifyResetPasswordUrl(String key);

    void setNewPassword(String key, String password, String confirmPassword);

    UserDTO verifyAccount(String key);

    UserDTO updateUserDetails(UpdateForm user);

    UserDTO getUserById(Long userId);

    void updatePassword(Long id, String currentPassword, String newPassword, String confirmNewPassword);
    void updatePasswordBeingLoggedOut(Long userId, String newPassword, String confirmNewPassword);

    void updateSettings(Long id, boolean enabled, boolean notLocked);

    void updateUsingMfa(Long id);

    void updateImage(String email, MultipartFile image);
}
