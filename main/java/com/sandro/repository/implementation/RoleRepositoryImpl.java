package com.sandro.repository.implementation;

import com.sandro.domain.Role;
import com.sandro.domain.User;
import com.sandro.dto.UserDTO;
import com.sandro.exception.ApiException;
import com.sandro.repository.RoleRepository;
import com.sandro.rowmapper.RoleRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import static com.sandro.enumeration.RoleType.ROLE_USER;
import static com.sandro.query.RoleQuery.*;

/**
 * @author Alessandro Formica
 * @version 1.0
 * @since 05.06.2023
 */

@Repository
@RequiredArgsConstructor
@Slf4j
public class RoleRepositoryImpl implements RoleRepository<Role> {


    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public Role create(Role data) {
        return null;
    }

    @Override
    public Collection<Role> getList() {
        log.info("fetching all roles in RoleRepository's getList()");
        try {
            return jdbc.query(SELECT_ROLES_QUERY,  new RoleRowMapper());
        } catch(EmptyResultDataAccessException exception) {
            throw new ApiException("No roles found");
        } catch(Exception exception) {
            throw new ApiException("An error occurred in getting list of roles. Please try again.");
        }
    }

    @Override
    public Role get(Long id) {
        return null;
    }

    @Override
    public Role update(Role data) {
        return null;
    }

    @Override
    public Boolean delete(Long id) {
        return null;
    }

    @Override
    public void addRoleToUser(Long userId, String roleName) {
        log.info("Add role: {} to user id: {}", roleName, userId);
        try {
            Role role = jdbc.queryForObject(SELECT_ROLE_BY_NAME_QUERY, Map.of("roleName", roleName), new RoleRowMapper());
            jdbc.update(INSERT_ROLE_TO_USER_QUERY, Map.of("userId", userId, "roleId", Objects.requireNonNull(role).getId()));

        } catch(EmptyResultDataAccessException exception) {
            throw new ApiException("No role found by name: " + ROLE_USER.name());
        } catch(Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }

    }

    @Override
    public Role getRoleByUserId(Long userId) {
        log.info("Fetch role to user id: {}", userId);
        try {
            return jdbc.queryForObject(SELECT_ROLE_BY_USER_ID_QUERY, Map.of("userId", userId), new RoleRowMapper());
        } catch(EmptyResultDataAccessException exception) {
            throw new ApiException("No role found by name: " + ROLE_USER.name());
        } catch(Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }

    }

    @Override
    public Role getRoleByUserEmail(String email) {
        return null;
    }

     @Override
    public void updateRoleByUserId(Long id, String roleName) {
         log.info("Updating role for user");
         try {
             jdbc.update(UPDATE_USER_ROLE_BY_USER_ID_QUERY, Map.of("userId", id, "roleName", roleName));
         }  catch(Exception exception) {
             log.error(exception.getMessage());
             throw new ApiException("An error occurred. Please try again.");
         }
    }
}
