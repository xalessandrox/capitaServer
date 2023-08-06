package com.sandro.rowmapper;

import com.sandro.domain.UserEvent;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Alessandro Formica
 * @version 1.0
 * @since 04.08.2023
 */


public class UserEventRowMapper implements RowMapper<UserEvent> {
    /**
     *
     */
    @Override
    public UserEvent mapRow(ResultSet rs, int rowNum) throws SQLException {
        return UserEvent.builder()
                .type(rs.getString("type"))
                .description(rs.getString("description"))
                .device(rs.getString("device"))
                .ipAddress(rs.getString("ip_address"))
                .createdAt(rs.getString("created_at"))
                .build();
    }
}
