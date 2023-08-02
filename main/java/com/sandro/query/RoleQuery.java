package com.sandro.query;

/**
 * @author Alessandro Formica
 * @version 1.0
 * @since 05.06.2023
 */


public class RoleQuery {

    public static final String INSERT_ROLE_TO_USER_QUERY =
            "INSERT INTO users_roles (user_id, role_id) VALUES (:userId, :roleId)";
    public static final String SELECT_ROLE_BY_NAME_QUERY =
            "SELECT * FROM roles WHERE name = :roleName";
    public static final String SELECT_ROLE_BY_USER_ID_QUERY =
            "SELECT r.id, r.name, r.permission " +
                    "FROM roles r " +
                    "JOIN users_roles ur ON ur.role_id = r.id " +
                    "JOIN users u ON ur.user_id = u.id " +
                    "WHERE u.id = :userId";
    public static final String SELECT_ROLES_QUERY = "SELECT * FROM roles ORDER BY id";
    public static final String UPDATE_USER_ROLE_BY_USER_ID_QUERY =
            "UPDATE users_roles SET role_id = (SELECT id FROM roles WHERE name = :roleName) WHERE user_id = :userId";
}
