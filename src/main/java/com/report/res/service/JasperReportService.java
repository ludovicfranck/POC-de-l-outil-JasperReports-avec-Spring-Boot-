package com.report.res.service;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import com.report.res.model.Employee;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JasperReportService {

    public byte[] generateSimpleReport() throws FileNotFoundException, JRException {
        // Charge le fichier JRXML
        File file = ResourceUtils.getFile("classpath:reports/simple_report.xml");
        JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
        
        // Paramètres (optionnel)
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("createdBy", "Spring Boot");
        
        // Source de données (vide dans cet exemple)
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(List.of());
        
        // Génère le rapport
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        
        // Export en PDF
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
    
    public byte[] generateEmployeeReport(List<Employee> employees) throws FileNotFoundException, JRException {
        File file = ResourceUtils.getFile("classpath:reports/employees_report.xml");
        JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
        
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(employees);
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("title", "Employee Report");
        
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
    
    
}