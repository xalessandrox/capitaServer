package com.sandro.repository;

import com.sandro.domain.Customer;
import com.sandro.domain.Statistics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Alessandro Formica
 * @version 1.0
 * @since 06.08.2023
 */

@Repository
public interface CustomerRepository extends PagingAndSortingRepository<Customer, Long>, ListCrudRepository<Customer, Long> {

    Page<Customer> findByLastNameContaining(String lastName, Pageable pageable);
    Page<Customer> findByLastNameContainingIgnoreCase(String lastName, Pageable pageable);


}
