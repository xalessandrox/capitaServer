package com.sandro.repository;

import com.sandro.domain.User;
import com.sandro.dto.UserDTO;
import com.sandro.form.UpdateForm;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;

/**
 * @author Alessandro Formica
 * @version 1.0
 * @since 04/06/2023
 */


public interface UserRepository<T extends User> {
    /* Basic CRUD Operations */

    T create(T data);

    Collection<T> list(int page, int pageSize);

    T get(Long id);

    T update(T data);

    Boolean delete(Long id);

    /* More complex operations */
    User getUserByEmail(String email);
    void sendVerificationCode(UserDTO user);

    T verifyCode(String email, String verificationCode);

    void resetPassword(String email);

    T verifyResetPasswordUrl(String key);

    void updatePasswordBeingLoggedOut(Long userId, String password, String confirmPassword);
    void updatePassword(String key, String newPassword, String confirmPassword);

    T verifyAccount(String key);

    T updateUserDetails(UpdateForm user);

    void updatePassword(Long id, String currentPassword, String newPassword, String confirmNewPassword);

    void updateUserSettings(Long id, boolean enabled, boolean notLocked);

    void updateUsingMfa(Long id);

    void updateImage(String email, MultipartFile image);
}
