# POC-de-l-outil-JasperReports-avec-Spring-Boot-

## 1.Structure de Projet
Voici un exemple de structure de projet typique :
```
src/
├── main/
│   ├── java/
│   │   └── com/example/jasperdemo/
│   │       ├── config/
│   │       ├── controller/
│   │       ├── model/
│   │       ├── repository/
│   │       ├── service/
│   │       └── JasperDemoApplication.java
│   └── resources/
│       ├── reports/          # Dossier pour les fichiers .jrxml
│       ├── static/           # Fichiers statiques
│       └── templates/        # Fichiers de template

```

## 2.Modele de données
```java
public class Employee {
    private Long id;
    private String name;
    private String department;
    private Double salary;

    // Constructeurs, getters et setters
    public Employee(Long id, String name, String department, Double salary) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.salary = salary;
    }

    // Getters et setters..(code Boilerplate)
}

```

## 3.Créer un rapport avec données (employees_report.jrxml)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<jasperReport xmlns="http://jasperreports.sourceforge.net/jasperreports" 
              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
              xsi:schemaLocation="http://jasperreports.sourceforge.net/jasperreports http://jasperreports.sourceforge.net/xsd/jasperreport.xsd" 
              name="employees_report" 
              pageWidth="595" 
              pageHeight="842" 
              columnWidth="555" 
              leftMargin="20" 
              rightMargin="20" 
              topMargin="20" 
              bottomMargin="20">
    <field name="id" class="java.lang.Long"/>
    <field name="name" class="java.lang.String"/>
    <field name="department" class="java.lang.String"/>
    <field name="salary" class="java.lang.Double"/>
    
    <title>
        <band height="50">
            <staticText>
                <reportElement x="0" y="0" width="555" height="30"/>
                <text><![CDATA[Employee Report]]></text>
            </staticText>
        </band>
    </title>
    
    <columnHeader>
        <band height="30">
            <staticText>
                <reportElement x="0" y="0" width="100" height="30"/>
                <text><![CDATA[ID]]></text>
            </staticText>
            <staticText>
                <reportElement x="100" y="0" width="200" height="30"/>
                <text><![CDATA[Name]]></text>
            </staticText>
            <staticText>
                <reportElement x="300" y="0" width="150" height="30"/>
                <text><![CDATA[Department]]></text>
            </staticText>
            <staticText>
                <reportElement x="450" y="0" width="100" height="30"/>
                <text><![CDATA[Salary]]></text>
            </staticText>
        </band>
    </columnHeader>
    
    <detail>
        <band height="30">
            <textField>
                <reportElement x="0" y="0" width="100" height="30"/>
                <textFieldExpression><![CDATA[$F{id}]]></textFieldExpression>
            </textField>
            <textField>
                <reportElement x="100" y="0" width="200" height="30"/>
                <textFieldExpression><![CDATA[$F{name}]]></textFieldExpression>
            </textField>
            <textField>
                <reportElement x="300" y="0" width="150" height="30"/>
                <textFieldExpression><![CDATA[$F{department}]]></textFieldExpression>
            </textField>
            <textField>
                <reportElement x="450" y="0" width="100" height="30"/>
                <textFieldExpression><![CDATA[$F{salary}]]></textFieldExpression>
            </textField>
        </band>
    </detail> 
</jasperReport>

```
## 4 Implementer un service de generation des Rapports

### 4.1 Creeons un service de generation des Rapports
```java
package com.example.jasperdemo.service;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JasperReportService {

    public byte[] generateSimpleReport() throws FileNotFoundException, JRException {
        // Charge le fichier JRXML
        File file = ResourceUtils.getFile("classpath:reports/simple_report.jrxml");
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
}

```
### 4.2 Ajoutons une methode au service de generation de rapport 

```java
    public byte[] generateEmployeeReport(List<Employee> employees) throws FileNotFoundException, JRException {
    File file = ResourceUtils.getFile("classpath:reports/employees_report.jrxml");
    JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
    
    JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(employees);
    
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("title", "Employee Report");
    
    JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
    
    return JasperExportManager.exportReportToPdf(jasperPrint);
}

```

## 5 Definition du controller pour exposer l'Api

### 5.1 Creation d'un ReportController
```java
package com.example.jasperdemo.controller;

import com.example.jasperdemo.service.JasperReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private JasperReportService jasperReportService;

    @GetMapping("/simple")
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
}

``` 
### 5.2 Ajouter un Endpoint au controller

```java
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

```

## 6 Tester l'pplication


Lancez votre application Spring Boot, puis accédez aux endpoints suivants via un navigateur ou un outil comme Postman :

- [http://localhost:8080/api/reports/simple](http://localhost:8080/api/reports/simple)  
  ➤ Génère le **rapport simple**.

- [http://localhost:8080/api/reports/employees](http://localhost:8080/api/reports/employees)  
  ➤ Génère le **rapport avec données dynamiques**.

---

## 7. Options Avancées

### 7.1 Export dans différents formats

Ajoutez la méthode suivante dans votre service pour permettre l’exportation en **PDF**, **XLSX**, ou **HTML** :

```java
public byte[] exportReportToFormat(JasperPrint jasperPrint, String format) throws JRException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    switch (format.toLowerCase()) {
        case "pdf":
            JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
            break;
        case "xlsx":
            JRXlsxExporter exporterXLSX = new JRXlsxExporter();
            exporterXLSX.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporterXLSX.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
            exporterXLSX.exportReport();
            break;
        case "html":
            JasperExportManager.exportReportToHtmlStream(jasperPrint, outputStream);
            break;
        default:
            throw new IllegalArgumentException("Format non supporté: " + format);
    }

    return outputStream.toByteArray();
}

```
### 7.2 Sous Rapport 

Pour les rapports complexes, vous pouvez créer des sous-rapports en:
- Créant un fichier JRXML séparé pour le sous-rapport
- Compilant les deux rapports
- Passant le sous-rapport compilé (.jasper) comme paramètre au rapport principal


## Conclusion

Ce Poc montre :
1.	Configurer JasperReports avec Spring Boot
2.	Créer des rapports simples et avec données dynamiques
3.	Exposer les rapports via une API REST
4.	Exporter dans différents formats
    Vous pouvez étendre cette base en ajoutant:
    - Des graphiques et visualisations
    - Des sous-rapports
    - Des paramètres dynamiques
    - Une mise en page plus complexe
    - Une génération asynchrone de rapports
    - Un stockage des rapports générés

