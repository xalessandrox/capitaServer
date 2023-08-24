package com.sandro.service;

import com.sandro.domain.Customer;
import com.sandro.domain.Statistics;
import org.springframework.data.domain.Page;

/**
 * @author Alessandro Formica
 * @version 1.0
 * @since 06.08.2023
 */


public interface CustomerService {

    Customer createCustomer(Customer customer);
    Customer updateCustomer(Customer customer);
    Page<Customer> getCustomersByPage(int page, int size);
    Iterable<Customer> getCustomers();
    Customer getCustomer(Long id);
    Page<Customer> searchCustomers(String lastName, int page, int size);


    Statistics getStatistics();
}
