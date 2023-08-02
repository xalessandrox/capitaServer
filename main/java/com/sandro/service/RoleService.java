package com.sandro.service;

import com.sandro.domain.Role;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * @author Alessandro Formica
 * @version 1.0
 * @since 03.07.2023
 */

@Service
public interface RoleService {
    Role getRoleByUserId(Long id);
    Collection<Role> getRoles();

    void updateRole(Long id, String roleName);
}
