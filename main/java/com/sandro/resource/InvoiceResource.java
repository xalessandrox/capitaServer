package com.sandro.resource;

import com.sandro.domain.Customer;
import com.sandro.domain.HttpResponse;
import com.sandro.domain.Invoice;
import com.sandro.dto.UserDTO;
import com.sandro.service.CustomerService;
import com.sandro.service.InvoiceService;
import com.sandro.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

/**
 * @author Alessandro Formica
 * @version 1.0
 * @since 06.08.2023
 */

@RestController
@RequestMapping(path = "/invoices")
@RequiredArgsConstructor
@Slf4j

public class InvoiceResource {

    private final UserService userService;
    private final InvoiceService invoiceService;
    private final CustomerService customerService;

//    @PostMapping("/new")
//    public ResponseEntity<HttpResponse> createCustomer(@AuthenticationPrincipal UserDTO userDTO,
//                                                       @RequestBody Invoice invoice) {
//        return ResponseEntity
//                .created(URI.create(""))
//                .body(
//                        HttpResponse.builder()
//                                .timeStamp(LocalDateTime.now().toString())
//                                .data(Map.of(
//                                        "user", userService.getUserByEmail(userDTO.getEmail()),
//                                        "invoice", invoiceService.createInvoice(invoice)))
//                                .message("Invoice created")
//                                .httpStatus(HttpStatus.CREATED)
//                                .statusCode(HttpStatus.CREATED.value())
//                                .build());
//    }

    @GetMapping("/new")
    public ResponseEntity<HttpResponse> createCustomer(@AuthenticationPrincipal UserDTO userDTO) {
        return ResponseEntity
                .created(URI.create(""))
                .body(
                        HttpResponse.builder()
                                .timeStamp(LocalDateTime.now().toString())
                                .data(Map.of(
                                        "user", userService.getUserByEmail(userDTO.getEmail()),
                                        "customers", customerService.getCustomers()))
                                .message("Added new invoice")
                                .httpStatus(HttpStatus.OK)
                                .statusCode(HttpStatus.OK.value())
                                .build());
    }

    @GetMapping("/list/paged")
    public ResponseEntity<HttpResponse> getInvoicesByPage(@AuthenticationPrincipal UserDTO userDTO,
                                                          @RequestParam Optional<Integer> page,
                                                          @RequestParam Optional<Integer> size) {
        return ResponseEntity
                .ok(
                        HttpResponse.builder()
                                .timeStamp(LocalDateTime.now().toString())
                                .data(Map.of(
                                        "user", userService.getUserByEmail(userDTO.getEmail()),
                                        "invoices", invoiceService.getInvoicesByPage(page.orElse(0), size.orElse(10))
                                ))
                                .message("Invoices retrieved")
                                .httpStatus(HttpStatus.OK)
                                .statusCode(HttpStatus.OK.value())
                                .build());
    }

    @GetMapping("/{invoiceId}")
    public ResponseEntity<HttpResponse> getCustomer(@AuthenticationPrincipal UserDTO userDTO,
                                                    @PathVariable("invoiceId") Long invoiceId) {
        Invoice invoice = invoiceService.getInvoice(invoiceId);
        Customer customer = invoice.getCustomer();
        return ResponseEntity
                .ok(
                        HttpResponse.builder()
                                .timeStamp(LocalDateTime.now().toString())
                                .data(Map.of(
                                        "user", userService.getUserByEmail(userDTO.getEmail()),
                                        "invoice", invoice,
                                        "customer", customer))
                                .message("Invoice retrieved")
                                .httpStatus(HttpStatus.OK)
                                .statusCode(HttpStatus.OK.value())
                                .build());
    }

    @PostMapping("/addCustomer/{id}")
    public ResponseEntity<HttpResponse> addCustomerToInvoice(@AuthenticationPrincipal UserDTO userDTO,
                                                             @PathVariable("id") Long id,
                                                             @RequestBody Invoice invoice) {
        invoiceService.addCustomerToInvoice(id, invoice);
        return ResponseEntity
                .created(URI.create(""))
                .body(
                        HttpResponse.builder()
                                .timeStamp(LocalDateTime.now().toString())
                                .data(Map.of(
                                        "user", userService.getUserByEmail(userDTO.getEmail()),
                                        "invoice", invoiceService.createInvoice(invoice)))
                                .message("Invoice created")
                                .httpStatus(HttpStatus.CREATED)
                                .statusCode(HttpStatus.CREATED.value())
                                .build());
    }

}
