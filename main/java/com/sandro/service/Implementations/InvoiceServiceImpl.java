package com.sandro.service.Implementations;

import com.sandro.domain.Customer;
import com.sandro.domain.Invoice;
import com.sandro.repository.CustomerRepository;
import com.sandro.repository.InvoiceRepository;
import com.sandro.service.InvoiceService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * @author Alessandro Formica
 * @version 1.0
 * @since 06.08.2023
 */

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor

public class InvoiceServiceImpl implements InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;
    @Override
    public void addCustomerToInvoice(Long customerId, Invoice invoice) {
        Customer customer = customerRepository.findById(customerId).get();
        invoice.setCustomer(customer);
        invoiceRepository.save(invoice);
    }

    @Override
    public Invoice createInvoice(Invoice invoice) {
        invoice.setInvoiceNumber(
                LocalDate.now().format(DateTimeFormatter.ofPattern("yy_M_d")) + "-" + RandomStringUtils.randomAlphabetic(10) + "-" + RandomStringUtils.randomNumeric(2).toLowerCase());
        return invoiceRepository.save(invoice);
    }

    @Override
    public Page<Invoice> getInvoicesByPage(int page, int size) {
        return invoiceRepository.findAll(PageRequest.of(page, size));
    }

    @Override
    public Invoice getInvoice(Long id) {
        return invoiceRepository.findById(id).orElseThrow();
    }
}
