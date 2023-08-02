package com.sandro.repository;

import com.sandro.domain.Role;
import org.springframework.stereotype.Repository;

import java.util.Collection;

/**
 * @author Alessandro Formica
 * @version 1.0
 * @since 04.06.2023
 */

@Repository
public interface RoleRepository<T extends Role> {

    T create(T data);

    Collection<T> getList();

    T get(Long id);

    T update(T data);

    Boolean delete(Long id);

    /* More Complex Operations */
    void addRoleToUser(Long userId, String roleName);
    Role getRoleByUserId(Long id);
    Role getRoleByUserEmail(String email);
    void updateRoleByUserId(Long id, String roleName);
}
