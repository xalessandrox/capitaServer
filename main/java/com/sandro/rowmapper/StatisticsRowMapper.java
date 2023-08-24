package com.sandro.rowmapper;

import com.sandro.domain.Statistics;
import com.sandro.domain.UserEvent;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Alessandro Formica
 * @version 1.0
 * @since 14.08.2023
 */


public class StatisticsRowMapper implements RowMapper<Statistics> {

    @Override
    public Statistics mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Statistics.builder()
                .totalCustomers(rs.getInt("tot_customers"))
                .totalInvoices(rs.getInt("tot_invoices_count"))
                .totalBilled(rs.getInt("tot_invoices_sum"))
                .build();
    }
}
