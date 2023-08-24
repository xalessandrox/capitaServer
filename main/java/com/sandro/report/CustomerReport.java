package com.sandro.report;

import com.sandro.domain.Customer;
import com.sandro.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.InputStreamResource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.IntStream;

/**
 * @author Alessandro Formica
 * @version 1.0
 * @since 22.08.2023
 */

@Slf4j
public class CustomerReport {

    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<Customer> customers;
    private final String[] HEADERS = {"Id", "First Name", "Last Name", "Email", "Type", "Status", "Address", "Phone", "Created at"};

    public CustomerReport(List<Customer> customers) {
        this.customers = customers;
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("Customers");
        setHeaders();
    }

    private void setHeaders() {
        Row headerRow = sheet.createRow(0);
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(14);
        CellStyle style = workbook.createCellStyle();
        style.setFont(font);

        IntStream.range(0, HEADERS.length).forEach(index -> {
            Cell cell = headerRow.createCell(index);
            cell.setCellValue(HEADERS[index]);
            cell.setCellStyle(style);
        });
    }

    public InputStreamResource export() {
        return generateReport();
    }

    private InputStreamResource generateReport() {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            XSSFFont font = workbook.createFont();
            font.setFontHeight(10);
            CellStyle style = workbook.createCellStyle();
            style.setFont(font);
            int rowIndex = 1;
            for (Customer customer : customers) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(customer.getId());
                row.createCell(1).setCellValue(customer.getFirstName());
                row.createCell(2).setCellValue(customer.getLastName());
                row.createCell(3).setCellValue(customer.getEmail());
                row.createCell(4).setCellValue(customer.getType());
                row.createCell(5).setCellValue(customer.getStatus());
                row.createCell(6).setCellValue(customer.getAddress());
                row.createCell(7).setCellValue(customer.getPhone());
                row.createCell(8).setCellValue(customer.getCreatedAt());
            }
            workbook.write(out);
            return new InputStreamResource(new ByteArrayInputStream(out.toByteArray()));
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            throw new ApiException("Unable to export report file");
        }
    }

}
