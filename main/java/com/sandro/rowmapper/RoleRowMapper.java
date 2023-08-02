package com.sandro.rowmapper;

import com.sandro.domain.Role;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Alessandro Formica
 * @version 1.0
 * @since 05.06.2023
 */


public class RoleRowMapper implements RowMapper<Role> {
    @Override
    public Role mapRow(ResultSet rs, int rowNum) throws SQLException {

        return Role.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .permissions(rs.getString("permission"))
                .build();
    }
}
