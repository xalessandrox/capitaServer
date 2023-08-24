package com.sandro.query;

/**
 * @author Alessandro Formica
 * @version 1.0
 * @since 14.08.2023
 */


public class StatisticsQuery {

    public static final String STATS_QUERY = """
            SELECT c.tot_customers, inv.tot_invoices_count, inv_sum.tot_invoices_sum FROM 
            (SELECT COUNT(*) tot_customers FROM customers) c,
            (SELECT COUNT(*) tot_invoices_count FROM invoices) inv,
            (SELECT ROUND(SUM(total)) tot_invoices_sum FROM invoices) inv_sum;
            """;

}
