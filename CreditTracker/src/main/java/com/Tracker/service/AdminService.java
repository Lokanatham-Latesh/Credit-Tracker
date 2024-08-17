package com.Tracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Tracker.CreditRepositrory.CreditData1stYearRepo;
import com.Tracker.CreditRepositrory.CreditData2ndYearRepo;
import com.Tracker.CreditRepositrory.CreditData3rdYearRepo;
import com.Tracker.CreditRepositrory.CreditData4thYearRepo;
import com.Tracker.CreditRepositrory.SubjectRepoCIVIL;
import com.Tracker.CreditRepositrory.SubjectRepoCSE;
import com.Tracker.CreditRepositrory.SubjectRepoECE;
import com.Tracker.CreditRepositrory.SubjectRepoEEE;
import com.Tracker.CreditRepositrory.SubjectRepoMECH;

@Service
public class AdminService {
	
	

	@Autowired 
	private SubjectRepoCSE subjectRepoCSE;
	
	@Autowired 
	private SubjectRepoECE subjectRepoECE;
	
	@Autowired 
	private SubjectRepoEEE subjectRepoEEE;
	
	@Autowired 
	private SubjectRepoMECH subjectRepoMECH;
	
	@Autowired 
	private SubjectRepoCIVIL subjectRepoCIVIL;
	
	 @Autowired
	    private CreditData1stYearRepo creditData1stYearRepo;

	    @Autowired
	    private CreditData2ndYearRepo creditData2ndYearRepo;

	    @Autowired
	    private CreditData3rdYearRepo creditData3rdYearRepo;

	    @Autowired
	    private CreditData4thYearRepo creditData4thYearRepo;
	

	
	    public boolean deleteStudentData(int year, String sem) {
	        try {
	            if (sem != null) {
	                // Deletion logic based on year and semester
	                switch (year) {
	                    case 1:
	                        creditData1stYearRepo.deleteBySem(sem);
	                        break;
	                    case 2:
	                        creditData2ndYearRepo.deleteBySem(sem);
	                        break;
	                    case 3:
	                        creditData3rdYearRepo.deleteBySem(sem);
	                        break;
	                    case 4:
	                        creditData4thYearRepo.deleteBySem(sem);
	                        break;
	                    default:
	                        throw new IllegalArgumentException("Invalid year value. It must be one of 1, 2, 3, or 4.");
	                }
	            } else {
	                // Deletion logic based on year only
	                switch (year) {
	                    case 1:
	                        creditData1stYearRepo.truncateTable();;
	                        break;
	                    case 2:
	                        creditData2ndYearRepo.truncateTable();;
	                        break;
	                    case 3:
	                        creditData3rdYearRepo.truncateTable();;
	                        break;
	                    case 4:
	                        creditData4thYearRepo.truncateTable();;
	                        break;
	                    default:
	                        throw new IllegalArgumentException("Invalid year value. It must be one of 1, 2, 3, or 4.");
	                }
	            }
	            return true;
	        } catch (Exception e) {
	            // logger.error("Error deleting student data for year {} and sem {}: {}", year, sem, e.getMessage());
	            return false;
	        }
	    }
  
	   public boolean deleteSubjectData(String branch) {
           try {
               switch (branch) {
                   case "CSE":
                	   subjectRepoCSE.deleteAll();
                       break;
                   case "EEE":
                	   subjectRepoEEE.deleteAll();
                       break;
                   case "ECE":
                	   subjectRepoECE.deleteAll();
                       break;
                   case "MECH":
                	   subjectRepoMECH.deleteAll();
                       break;
                   case "CIVIL":
                	   subjectRepoCIVIL.deleteAll();
                       break;
                   default:
                       throw new IllegalArgumentException("Invalid branch value.");
               }
               return true;
           } catch (Exception e) {
           //    logger.error("Error deleting student data for year {}: {}", year, e.getMessage());
               return false;
           }
       }
       
    

}
