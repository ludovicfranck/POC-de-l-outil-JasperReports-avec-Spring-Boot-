package com.report.res.controller;

import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.report.res.model.Employee;
import com.report.res.service.JasperReportService;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
	
	@Autowired
    private JasperReportService jasperReportService;
	
	@GetMapping("/")
	public String welcomeHomeMessage() {
		return "Welcome to the home page";
	}

    @GetMapping("/simple_report")
    public ResponseEntity<byte[]> generateSimpleReport() {
        try {
            byte[] reportContent = jasperReportService.generateSimpleReport();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "simple_report.pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(reportContent);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    
    @GetMapping("/employees")
    public ResponseEntity<byte[]> generateEmployeeReport() {
        List<Employee> employees = List.of(
            new Employee(1L, "John Doe", "IT", 5000.0),
            new Employee(2L, "Jane Smith", "HR", 4500.0),
            new Employee(3L, "Robert Johnson", "Finance", 6000.0)
        );
        
        try {
            byte[] reportContent = jasperReportService.generateEmployeeReport(employees);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "employees_report.pdf");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(reportContent);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}
