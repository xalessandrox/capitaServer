package com.sandro.service;

import com.sandro.domain.Invoice;
import org.springframework.data.domain.Page;

/**
 * @author Alessandro Formica
 * @version 1.0
 * @since 06.08.2023
 */


public interface InvoiceService {

    void addCustomerToInvoice(Long userId, Invoice invoice);
    Invoice createInvoice(Invoice invoice);
    Page<Invoice> getInvoicesByPage(int page, int size);
    Invoice getInvoice(Long id);

}
