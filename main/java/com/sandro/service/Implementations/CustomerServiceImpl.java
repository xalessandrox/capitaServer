package com.sandro.service.Implementations;

import com.sandro.domain.Customer;
import com.sandro.domain.Statistics;
import com.sandro.query.StatisticsQuery;
import com.sandro.repository.CustomerRepository;
import com.sandro.repository.InvoiceRepository;
import com.sandro.rowmapper.StatisticsRowMapper;
import com.sandro.service.CustomerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

/**
 * @author Alessandro Formica
 * @version 1.0
 * @since 06.08.2023
 */

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor

public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final NamedParameterJdbcTemplate jdbc;


    @Override
    public Customer createCustomer(Customer customer) {
        customer.setCreatedAt(new Date());
        return customerRepository.save(customer);
    }

    @Override
    public Customer updateCustomer(Customer customer) {
        return customerRepository.save(customer);

    }

    @Override
    public Page<Customer> getCustomersByPage(int page, int size) {
        return customerRepository.findAll(PageRequest.of(page, size));
    }

    @Override
    public Iterable<Customer> getCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public Customer getCustomer(Long id) {
        return customerRepository.findById(id).get();
    }

    @Override
    public Page<Customer> searchCustomers(String lastName, int page, int size) {
        return customerRepository.findByLastNameContainingIgnoreCase(lastName, PageRequest.of(page, size));
    }

    @Override
    public Statistics getStatistics() {
        return jdbc.queryForObject(StatisticsQuery.STATS_QUERY, Map.of(), new StatisticsRowMapper());
    }
}
