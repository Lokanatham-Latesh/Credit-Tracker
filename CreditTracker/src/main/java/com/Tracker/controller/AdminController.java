package com.Tracker.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Tracker.service.AdminService;



@RestController  
@RequestMapping("/api/CreditTrack") 
@CrossOrigin(origins = "http://localhost:3000")

public class AdminController {
	
	
	@Autowired
	private AdminService adminService;
	

	@DeleteMapping("/delete-student-data")
	public ResponseEntity<?> deleteStudentData(
	        @RequestParam("year") int year,
	        @RequestParam(value = "sem", required = false) String sem) {
	    try {
	        // Validate the year parameter
	        if (year < 1 || year > 4) {
	            throw new IllegalArgumentException("Invalid year value. It must be one of 1, 2, 3, or 4.");
	        }

	        // Validate the sem parameter if provided
	        if (sem != null && !sem.matches("Sem [1-8]")) {
	            throw new IllegalArgumentException("Invalid sem value. It must be one of 'Sem 1' to 'Sem 8'.");
	        }

	        boolean isDeleted = adminService.deleteStudentData(year, sem);
	        if (isDeleted) {
	            String message = (sem != null) ?
	                    "Student data for year " + year + " and " + sem + " deleted successfully" :
	                    "Student data for year " + year + " deleted successfully";
	            return ResponseEntity.ok(Map.of("Message", message));
	        } else {
	            String error = (sem != null) ?
	                    "Failed to delete student data for year " + year + " and " + sem :
	                    "Failed to delete student data for year " + year;
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("Error", error));
	        }
	    } catch (IllegalArgumentException e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("Error", e.getMessage()));
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("Error", "Failed to delete student data", "Details", e.getMessage()));
	    }
	}

    @DeleteMapping("/delete-subject-data")
    public ResponseEntity<?> deleteSubjectData(@RequestParam("branch")String branch) {
        try {
            // Validate the year parameter
            String normalizedBranch = branch.toUpperCase();

            // Validate branch and year
            if (!isValidBranch(normalizedBranch)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid branch: " + branch);
            }
            
            boolean isDeleted = adminService.deleteSubjectData(branch);
            if (isDeleted) {
                return ResponseEntity
                    .ok(Map.of("Message", "Subject data for branch " + branch + " deleted successfully"));
            } else {
                return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("Error", "Failed to delete Subject data for branch " + branch));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("Error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("Error", "Failed to delete Subject data", "Details", e.getMessage()));
        }
    }
    
    private boolean isValidBranch(String branch) {
        List<String> validBranches = Arrays.asList("CSE", "ECE", "EEE", "MECH", "CIVIL");
        return validBranches.contains(branch);
    }



	


}
