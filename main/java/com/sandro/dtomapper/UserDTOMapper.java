package com.sandro.dtomapper;

import com.sandro.domain.Role;
import com.sandro.domain.User;
import com.sandro.dto.UserDTO;
import org.springframework.beans.BeanUtils;

/**
 * @author Alessandro Formica
 * @version 1.0
 * @since 05.06.2023
 */

public class UserDTOMapper {

    public static UserDTO fromUser(User user) {
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        return userDTO;
    }

    public static UserDTO fromUser(User user, Role role) {
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        userDTO.setRoleName(role.getName());
        userDTO.setPermissions(role.getPermissions());
        return userDTO;
    }

    public static User toUser(UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        return user;
    }

}
