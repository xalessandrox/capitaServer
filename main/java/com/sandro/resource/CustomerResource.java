package com.sandro.resource;

import com.sandro.domain.Customer;
import com.sandro.domain.HttpResponse;
import com.sandro.dto.UserDTO;
import com.sandro.report.CustomerReport;
import com.sandro.service.CustomerService;
import com.sandro.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Alessandro Formica
 * @version 1.0
 * @since 06.08.2023
 */

@RestController
@RequestMapping(path = "/customers")
@RequiredArgsConstructor
@Slf4j

public class CustomerResource {

    private final CustomerService customerService;
    private final UserService userService;


    @GetMapping("/list")
    public ResponseEntity<HttpResponse> getCustomersByPage(@AuthenticationPrincipal UserDTO userDTO,
                                                           @RequestParam Optional<Integer> page,
                                                           @RequestParam Optional<Integer> size) {
        return ResponseEntity
                .ok(
                        HttpResponse.builder()
                                .timeStamp(LocalDateTime.now().toString())
                                .data(Map.of(
                                        "user", userService.getUserByEmail(userDTO.getEmail()),
                                        "customers", customerService.getCustomersByPage(page.orElse(0), size.orElse(10)),
                                        "statistics", customerService.getStatistics()
                                ))
                                .message("Customers retrieved")
                                .httpStatus(HttpStatus.OK)
                                .statusCode(HttpStatus.OK.value())
                                .build());
    }

    @PostMapping("/new")
    public ResponseEntity<HttpResponse> createCustomer(@AuthenticationPrincipal UserDTO userDTO,
                                                       @RequestBody Customer customer) {

        return ResponseEntity
                .created(URI.create(""))
                .body(
                        HttpResponse.builder()
                                .timeStamp(LocalDateTime.now().toString())
                                .data(Map.of(
                                        "user", userService.getUserByEmail(userDTO.getEmail()),
                                        "customer", customerService.createCustomer(customer)))
                                .message("Customer created")
                                .httpStatus(HttpStatus.CREATED)
                                .statusCode(HttpStatus.CREATED.value())
                                .build());

    }

    @GetMapping("/{id}")
    public ResponseEntity<HttpResponse> getCustomer(@NotNull @AuthenticationPrincipal UserDTO userDTO,
                                                    @PathVariable("id") Long id) {
        return ResponseEntity
                .ok(
                        HttpResponse.builder()
                                .timeStamp(LocalDateTime.now().toString())
                                .data(Map.of(
                                        "user", userService.getUserByEmail(userDTO.getEmail()),
                                        "customer", customerService.getCustomer(id)))
                                .message("Customer retrieved")
                                .httpStatus(HttpStatus.OK)
                                .statusCode(HttpStatus.OK.value())
                                .build());
    }

    @GetMapping("/search")
    public ResponseEntity<HttpResponse> searchCustomer(@AuthenticationPrincipal UserDTO userDTO,
                                                       @RequestParam Optional<String> lastName,
                                                       @RequestParam Optional<Integer> page,
                                                       @RequestParam Optional<Integer> size) {
        return ResponseEntity
                .ok(
                        HttpResponse.builder()
                                .timeStamp(LocalDateTime.now().toString())
                                .data(Map.of(
                                        "user", userService.getUserByEmail(userDTO.getEmail()),
                                        "customers", customerService.searchCustomers(
                                                lastName.orElse(""), page.orElse(0), size.orElse(10)
                                        )))
                                .message("Customers retrieved")
                                .httpStatus(HttpStatus.OK)
                                .statusCode(HttpStatus.OK.value())
                                .build());
    }

    @PutMapping("/update")
    public ResponseEntity<HttpResponse> updateCustomer(@AuthenticationPrincipal UserDTO userDTO,
                                                       @RequestBody Customer customer) {
        return ResponseEntity
                .ok(
                        HttpResponse.builder()
                                .timeStamp(LocalDateTime.now().toString())
                                .data(Map.of(
                                        "user", userService.getUserByEmail(userDTO.getEmail()),
                                        "customer", customerService.updateCustomer(customer)))
                                .message("Customer updated")
                                .httpStatus(HttpStatus.OK)
                                .statusCode(HttpStatus.OK.value())
                                .build());
    }

    @GetMapping("/list/notpaged")
    public ResponseEntity<HttpResponse> getCustomers(@AuthenticationPrincipal UserDTO userDTO) {
        return ResponseEntity
                .ok(
                        HttpResponse.builder()
                                .timeStamp(LocalDateTime.now().toString())
                                .data(Map.of(
                                        "user", userService.getUserByEmail(userDTO.getEmail()),
                                        "customers", customerService.getCustomers()
                                ))
                                .message("Invoices retrieved")
                                .httpStatus(HttpStatus.OK)
                                .statusCode(HttpStatus.OK.value())
                                .build());
    }

    @GetMapping("/downloads/report")
    public ResponseEntity<Resource> downloadReport() {

        List<Customer> customers = new ArrayList<>();
        customerService.getCustomers().iterator().forEachRemaining(customers::add);
        CustomerReport report = new CustomerReport(customers);
        HttpHeaders headers = new HttpHeaders();
        headers.add("File-Name", "CustomerReport.xlsx");
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;File-Name=CustomerReport.xlsx");
        return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .headers(headers)
                .body(report.export());
    }

}
