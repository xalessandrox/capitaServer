package com.sandro.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * @author Alessandro Formica
 * @version 1.0
 * @since 14.08.2023
 */

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
//@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Statistics {

    private int totalCustomers;
    private int totalInvoices;
    private double totalBilled;

}
