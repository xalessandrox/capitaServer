package com.sandro.service.Implementations;

import com.sandro.domain.Role;
import com.sandro.domain.User;
import com.sandro.dto.UserDTO;
import com.sandro.dtomapper.UserDTOMapper;
import com.sandro.form.UpdateForm;
import com.sandro.repository.RoleRepository;
import com.sandro.repository.UserRepository;
import com.sandro.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author Alessandro Formica
 * @version 1.0
 * @since 05.06.2023
 */

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository<User> userRepository;
    private final RoleRepository<Role> roleRepository;

    @Override
    public UserDTO createUser(User user) {
        return mapToUserDto(userRepository.create(user));
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        return mapToUserDto(userRepository.getUserByEmail(email));
    }

    @Override
    public void sendVerificationCode(UserDTO user) {
        userRepository.sendVerificationCode(user);
    }


    @Override
    public UserDTO verifyCode(String email, String verificationCode) {
        return mapToUserDto(userRepository.verifyCode(email, verificationCode));
    }

    @Override
    public void resetPassword(String email) {
        userRepository.resetPassword(email);
    }

    @Override
    public UserDTO verifyResetPasswordUrl(String key) {
        return mapToUserDto(userRepository.verifyResetPasswordUrl(key));

    }

    @Override
    public void setNewPassword(String key, String password, String confirmPassword) {
        userRepository.setNewPassword(key, password, confirmPassword);
    }

    @Override
    public UserDTO verifyAccount(String key) {
        return mapToUserDto(userRepository.verifyAccount(key));
    }

    @Override
    public UserDTO updateUserDetails(UpdateForm user) {
        return mapToUserDto(userRepository.updateUserDetails(user));
    }

    @Override
    public UserDTO getUserById(Long userId) {
        return mapToUserDto(userRepository.get(userId));
    }

    @Override
    public void updatePassword(Long id, String currentPassword, String newPassword, String confirmNewPassword) {
        userRepository.updatePassword(id, currentPassword, newPassword,  confirmNewPassword);
    }

    @Override
    public void updateSettings(Long id, boolean enabled, boolean notLocked) {
        userRepository.updateUserSettings(id, enabled, notLocked);
    }

    public UserDTO mapToUserDto(User user) {
        return UserDTOMapper.fromUser(user, roleRepository.getRoleByUserId(user.getId()));
    }

}
