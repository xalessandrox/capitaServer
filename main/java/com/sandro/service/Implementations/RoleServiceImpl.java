package com.sandro.service.Implementations;

import com.sandro.domain.Role;
import com.sandro.repository.RoleRepository;
import com.sandro.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * @author Alessandro Formica
 * @version 1.0
 * @since 03.07.2023
 */

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

private final RoleRepository<Role> roleRepository;
    @Override
    public Role getRoleByUserId(Long id) {
        return roleRepository.getRoleByUserId(id);
    }

    @Override
    public Collection<Role> getRoles() {
        return roleRepository.getList();
    }

    @Override
    public void updateRole(Long id, String roleName) {
        roleRepository.updateRoleByUserId(id, roleName);
    }
}
