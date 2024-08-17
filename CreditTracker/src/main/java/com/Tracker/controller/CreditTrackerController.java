package com.Tracker.controller;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.http.HttpHeaders;


import com.Tracker.Entity.CreditData;
import com.Tracker.Entity.CreditData1stYear;
import com.Tracker.Entity.CreditData2ndYear;
import com.Tracker.Entity.SubjectData;
import com.Tracker.Exception.ResourceNotFoundException;
import com.Tracker.request.SubjectDataReq;
import com.Tracker.service.CreditTrackStudents1stYear;
import com.Tracker.service.CreditTrackStudents2ndYear;
import com.Tracker.service.CreditTrackStudents3rdYear;
import com.Tracker.service.CreditTrackStudents4thYear;
import com.Tracker.service.CreditTrackerService;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;





@RestController  
@RequestMapping("/api/CreditTrack") 
@CrossOrigin(origins = "http://localhost:3000")

public class CreditTrackerController {  
	 @Value("${creditTracker.template.report:/templates/report.vm}")
	    private String reportTemplate;
 
    @Autowired
    private CreditTrackerService creditTrackerService;
    
    
    @Autowired
    private CreditTrackStudents1stYear creditTrack1stStudents;
    
    @Autowired
    private CreditTrackStudents2ndYear creditTrackStudents2ndYear;
    
    @Autowired
    private CreditTrackStudents3rdYear creditTrackStudents3rdYear;
    
    
    @Autowired
    private CreditTrackStudents4thYear creditTrackStudents4thYear;
   
    
    @Value("${smsuite.template.stockTransfer_receipt:templates/report.vm}")
	private String stockTransferReceiptTemplate;

 
    @GetMapping("/generate/{regdNo}")
    public ResponseEntity<?> generateReport(
            @PathVariable String regdNo,
            @RequestParam String branch,
            @RequestParam int year) {
        ResponseEntity<?> response = getBasketCredits(regdNo, branch, year);
        
        if (!response.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(response.getStatusCode()).body(null);
        }

        List<Map<String, Object>> basketCredits = (List<Map<String, Object>>) response.getBody();
        List<?> studentDetailsList = creditTrackerService.getAllDetailsByRegdNo(regdNo, year);

        if (studentDetailsList.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("Error", "No data found for registration number: " + regdNo));
        }

        basketCredits = sortBasketCredits(basketCredits);  
        Map<String, Object> model = new HashMap<>();
        String highestSemester = creditTrackerService.getHighestSemesterByRegdNo(regdNo, year);
        
        model.put("studentDetails", studentDetailsList.get(0));
        model.put("basketCredits", basketCredits);
        model.put("branch", branch);
        model.put("year", year);
        model.put("highestSemester", highestSemester);

        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.setProperty("resource.loader", "classpath");
        velocityEngine.setProperty("classpath.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        velocityEngine.init();

        StringWriter writer = new StringWriter();
        Template template = velocityEngine.getTemplate(reportTemplate);
        template.merge(new VelocityContext(model), writer);
        String htmlContent = writer.toString();
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfDocument pdfDocument = new PdfDocument(new PdfWriter(out));
            ConverterProperties converterProperties = new ConverterProperties();
            HtmlConverter.convertToPdf(htmlContent, pdfDocument, converterProperties);
            pdfDocument.close();
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("Error", "Failed to generate the PDF report."));
        }

        ByteArrayInputStream inputStream = new ByteArrayInputStream(out.toByteArray());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=CreditTrackerReport-" + regdNo + ".pdf");
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));
    }

    @PostMapping("/upload-subject-data")
    public ResponseEntity<?> uploadSubjectsData(
            @RequestParam("file") MultipartFile file,
            @RequestParam("branch") String branch) {
        try {
          
            if (branch == null || branch.isEmpty()) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("Error", "Branch is required and cannot be empty"));
            }

            List<String> validBranches = List.of("CSE", "ECE", "EEE", "MECH", "CIVIL");
            if (!validBranches.contains(branch.toUpperCase())) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("Error", "Invalid branch value. Valid values are: " + validBranches));
            }

           
            creditTrackerService.saveSubjectsToDatabase(file, branch.toUpperCase());
            return ResponseEntity
                .ok(Map.of("Message", "Subjects data uploaded and saved to database successfully"));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("Error", "Failed to upload and save subjects data", "Details", e.getMessage()));
        }
    }
    
    private void validateBranch(String branch) {
        List<String> validBranches = List.of("CSE", "ECE", "EEE", "MECH", "CIVIL");
        if (!validBranches.contains(branch.toUpperCase())) {
            throw new IllegalArgumentException("Invalid branch: " + branch + ". Valid branches are: " + String.join(", ", validBranches));
        }
    }
    
    @PostMapping("/upload-student-data")
    public ResponseEntity<?> uploadStudentsData(@RequestParam("file") MultipartFile file, @RequestParam("year") int year) {
        try {
            // Validate the year parameter
            if (year < 1 || year > 4) {
                throw new IllegalArgumentException("Invalid year. It must be one of 1, 2, 3, or 4.");
            }

            creditTrackerService.saveStudentsToDatabase(file, year);
            return ResponseEntity
                .ok(Map.of("Message", "Students data uploaded and saved to database successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("Error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("Error", "Failed to upload and save students data", "Details", e.getMessage()));
        }
    }
   
   

    @GetMapping("/details/{regdNo}")
    public ResponseEntity<?> getDetails(@PathVariable String regdNo, @RequestParam("year") int year) {
        try {
            // Validate the year parameter
            if (year < 1 || year > 4) {
                throw new IllegalArgumentException("Invalid year value. It must be one of 1, 2, 3, or 4.");
            }
            List<?> creditDataList = creditTrackerService.getAllDetailsByRegdNo(regdNo, year);
            return ResponseEntity.ok(creditDataList);
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("Error", e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("Error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("Error", "Failed to retrieve student data", "Details", e.getMessage()));
        }
    }
    @GetMapping("/subject-basket/{regdNo}")
    public ResponseEntity<List<Map<String, Object>>> getSubjectBasketByRegdNo(
            @PathVariable String regdNo,
            @RequestParam String branch,
            @RequestParam int year) {
        try {
            validateBranch(branch);
            validateYear(year);
            List<Map<String, Object>> subjectBasketMap = creditTrackerService.getSubjectBasketAndCreditsByRegdNo(regdNo, branch, year);
            return ResponseEntity.ok(subjectBasketMap);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    private void validateYear(int year) {
        if (year < 1 || year > 4) {
            throw new IllegalArgumentException("Invalid year value. Must be one of: 1, 2, 3, 4.");
        }

}
    
    private List<Map<String, Object>> sortBasketCredits(List<Map<String, Object>> basketCredits) {  
        // Define the custom order for sorting  
        List<String> order = Arrays.asList("Basket I", "Basket II", "Basket III", "Basket IV", "Basket V");  

        // Create a hashmap for the order indexing  
        Map<String, Integer> orderMap = new HashMap<>();  
        for (int i = 0; i < order.size(); i++) {  
            orderMap.put(order.get(i), i);  
        }  

        // Sort the basket credits using a comparator  
        basketCredits.sort(Comparator.comparingInt(basket -> orderMap.getOrDefault(basket.get("Basket"), Integer.MAX_VALUE)));  

        return basketCredits;  
    }  
    
    @GetMapping("/basket-credits/{regdNo}")
    public ResponseEntity<?> getBasketCredits(
            @PathVariable String regdNo,
            @RequestParam String branch,
            @RequestParam int year) {
        try {
            List<Map<String, Object>> basketCredits;

            // Convert branch to uppercase for consistent comparison
            String normalizedBranch = branch.toUpperCase();

            // Validate branch and year
            if (!isValidBranch(normalizedBranch)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid branch: " + branch);
            }
            if (year < 1 || year > 4) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid year: " + year);
            }

            // Call the appropriate method based on the branch and year
            switch (normalizedBranch) {
                case "CSE":
                    switch (year) {
                        case 1:
                            basketCredits = creditTrack1stStudents.getCSE1stYearBasketCreditsByRegdNo(regdNo);
                            break;
                        case 2:
                            basketCredits = creditTrackStudents2ndYear.getCSE2ndYearBasketCreditsByRegdNo(regdNo);
                            break;
                        case 3:
                            basketCredits = creditTrackStudents3rdYear.getCSE3rdYearBasketCreditsByRegdNo(regdNo);
                            break;
                        case 4:
                            basketCredits = creditTrackStudents4thYear.getCSE4thYearBasketCreditsByRegdNo(regdNo);
                            break;
                        default:
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                    .body("Year " + year + " not supported for branch CSE");
                    }
                    break;
                case "ECE":
                    switch (year) {
                        case 1:
                            basketCredits = creditTrack1stStudents.getECEE1stYearBasketCreditsByRegdNo(regdNo);
                            break;
                        case 2:
                            basketCredits = creditTrackStudents2ndYear.getECE2ndYearBasketCreditsByRegdNo(regdNo);
                            break;
                        case 3:
                            basketCredits = creditTrackStudents3rdYear.getECE3rdYearBasketCreditsByRegdNo(regdNo);
                            break;
                        case 4:
                            basketCredits = creditTrackStudents4thYear.getECE4thYearBasketCreditsByRegdNo(regdNo);
                            break;
                        default:
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                    .body("Year " + year + " not supported for branch ECE");
                    }
                    break;
                case "EEE":
                    switch (year) {
                        case 1:
                            basketCredits = creditTrack1stStudents.getEEE1stYearBasketCreditsByRegdNo(regdNo);
                            break;
                        case 2:
                            basketCredits = creditTrackStudents2ndYear.getEEE2ndYearBasketCreditsByRegdNo(regdNo);
                            break;
                        case 3:
                            basketCredits = creditTrackStudents3rdYear.getEEE3rdYearBasketCreditsByRegdNo(regdNo);
                            break;
                        case 4:
                            basketCredits = creditTrackStudents4thYear.getEEE4thYearBasketCreditsByRegdNo(regdNo);
                            break;
                        default:
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                    .body("Year " + year + " not supported for branch EEE");
                    }
                    break;
                case "MECH":
                    switch (year) {
                        case 1:
                            basketCredits = creditTrack1stStudents.getMECH1stYearBasketCreditsByRegdNo(regdNo);
                            break;
                        case 2:
                            basketCredits = creditTrackStudents2ndYear.getMECH2ndYearBasketCreditsByRegdNo(regdNo);
                            break;
                        case 3:
                            basketCredits = creditTrackStudents3rdYear.getMECH3rdYearBasketCreditsByRegdNo(regdNo);
                            break;
                        case 4:
                            basketCredits = creditTrackStudents4thYear.getMECH4thYearBasketCreditsByRegdNo(regdNo);
                            break;
                        default:
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                    .body("Year " + year + " not supported for branch MECH");
                    }
                    break;
                case "CIVIL":
                    switch (year) {
                        case 1:
                            basketCredits = creditTrack1stStudents.getCIVIL1stYearBasketCreditsByRegdNo(regdNo);
                            break;
                        case 2:
                            basketCredits = creditTrackStudents2ndYear.getCIVIL2ndYearBasketCreditsByRegdNo(regdNo);
                            break;
                        case 3:
                            basketCredits = creditTrackStudents3rdYear.getCIVIL3rdYearBasketCreditsByRegdNo(regdNo);
                            break;
                        case 4:
                            basketCredits = creditTrackStudents4thYear.getCIVIL4thYearBasketCreditsByRegdNo(regdNo);
                            break;
                        default:
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                    .body("Year " + year + " not supported for branch CIVIL");
                    }
                    break;
                default:
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Invalid branch: " + branch);
            }

            // Return basket credits
            basketCredits = sortBasketCredits(basketCredits); 
            return ResponseEntity.ok(basketCredits);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Data not found for registration number: " + regdNo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An internal server error occurred.");
        }
    }
    private boolean isValidBranch(String branch) {
        List<String> validBranches = Arrays.asList("CSE", "ECE", "EEE", "MECH", "CIVIL");
        return validBranches.contains(branch);
    }


    
    @PostMapping("/upload-subject-dataform")
    public ResponseEntity<?> uploadSubjectsData(
           
            @Valid @RequestBody SubjectDataReq subjectData) {
        try {
          
            if (subjectData.getBranch() == null || subjectData.getBranch().isEmpty()) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("Error", "Branch is required and cannot be empty"));
            }

            List<String> validBranches = List.of("CSE", "ECE", "EEE", "MECH", "CIVIL");
            if (!validBranches.contains(subjectData.getBranch().toUpperCase())) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("Error", "Invalid branch value. Valid values are: " + validBranches));
            }

           
            creditTrackerService.saveSubjectsToDatabaseForm(subjectData.getBranch(),subjectData);
            return ResponseEntity
                .ok(Map.of("Message", "Subjects data  saved  successfully"));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("Error", "Failed to  save subjects data", "Details", e.getMessage()));
        }
    }

    
    
    
}
        
        
    