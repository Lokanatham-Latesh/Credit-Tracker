package com.Tracker.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.Tracker.CreditRepositrory.CreditData1stYearRepo;
import com.Tracker.CreditRepositrory.CreditData2ndYearRepo;
import com.Tracker.CreditRepositrory.CreditData3rdYearRepo;
import com.Tracker.CreditRepositrory.CreditData4thYearRepo;
import com.Tracker.CreditRepositrory.CreditRepo;

import com.Tracker.CreditRepositrory.SubjectRepo;
import com.Tracker.CreditRepositrory.SubjectRepoCIVIL;
import com.Tracker.CreditRepositrory.SubjectRepoCSE;
import com.Tracker.CreditRepositrory.SubjectRepoECE;
import com.Tracker.CreditRepositrory.SubjectRepoEEE;
import com.Tracker.CreditRepositrory.SubjectRepoMECH;


import com.Tracker.Entity.CreditData;
import com.Tracker.Entity.CreditData1stYear;
import com.Tracker.Entity.CreditData2ndYear;
import com.Tracker.Entity.CreditData3rdYear;
import com.Tracker.Entity.CreditData4thYear;
import com.Tracker.Entity.SubjectData;
import com.Tracker.Entity.SubjectDataCIVIL;
import com.Tracker.Entity.SubjectDataCSE;
import com.Tracker.Entity.SubjectDataECE;
import com.Tracker.Entity.SubjectDataEEE;
import com.Tracker.Entity.SubjectDataMECH;

import com.Tracker.Exception.ResourceNotFoundException;
import com.Tracker.request.SubjectDataReq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service  
public class CreditTrackerService {

	  
	@Autowired
	private CreditRepo creditRepo;
	
	@Autowired
	private SubjectRepo subjectRepo;
	
	
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
	
	
	
	
	
	
	private static final Logger logger = LoggerFactory.getLogger(CreditTrackerService.class);
	  public void saveStudentsToDatabase(MultipartFile file) {  
	        try {  
	            List<CreditData> creditDatas = getStudentsDataFromExcel(file.getInputStream());  
	            List<CreditData> updatedOrNewRecords = new ArrayList<>();  

  

	            // Save only new records  
	            this.creditRepo.saveAll(creditDatas);  
	        } catch (IOException e) {  
	            throw new IllegalArgumentException("The file is not a valid Excel file", e);  
	        }  
	    } 
	  
	  
	  
	  public void saveSubjectsToDatabase(MultipartFile file, String branch) {
		    try {
		        List<SubjectData> subjectDataList = getSubjectDataFromExcel(file.getInputStream());

		        switch (branch) {
		            case "CSE" -> {
		                List<SubjectDataCSE> subjectDataCSEList = subjectDataList.stream()
		                    .map(this::convertToSubjectDataCSE)
		                    .collect(Collectors.toList());
		                subjectRepoCSE.saveAll(subjectDataCSEList);
		            }
		            case "ECE" -> {
		                List<SubjectDataECE> subjectDataECEList = subjectDataList.stream()
		                    .map(this::convertToSubjectDataECE)
		                    .collect(Collectors.toList());
		                subjectRepoECE.saveAll(subjectDataECEList);
		            }
		            case "EEE" -> {
		                List<SubjectDataEEE> subjectDataEEEList = subjectDataList.stream()
		                    .map(this::convertToSubjectDataEEE)
		                    .collect(Collectors.toList());
		                subjectRepoEEE.saveAll(subjectDataEEEList);
		            }
		            case "MECH" -> {
		                List<SubjectDataMECH> subjectDataMECHList = subjectDataList.stream()
		                    .map(this::convertToSubjectDataMECH)
		                    .collect(Collectors.toList());
		                subjectRepoMECH.saveAll(subjectDataMECHList);
		            }
		            case "CIVIL" -> {
		                List<SubjectDataCIVIL> subjectDataCIVILList = subjectDataList.stream()
		                    .map(this::convertToSubjectDataCIVIL)
		                    .collect(Collectors.toList());
		                subjectRepoCIVIL.saveAll(subjectDataCIVILList);
		            }
		            default -> throw new IllegalArgumentException("Invalid branch value");
		        }
		    } catch (IOException e) {
		        throw new IllegalArgumentException("The file is not a valid Excel file");
		    }
		}


	  
	  public boolean deleteStudentData() {
		    try {
		        
		        		
		    	creditRepo.deleteAll();
		        		return true;
		    } catch (Exception e) {
		        		        System.err.println("Error deleting student data: " + e.getMessage());
		       
		        return false;
		    }
		}
  
	  
	  public static List<CreditData> getStudentsDataFromExcel(InputStream inputStream){
	        List<CreditData> creditDatas = new ArrayList<>();
	       try {
	           XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
	           XSSFSheet sheet = workbook.getSheet("EVEN");
	           int rowIndex =0;
	           for (Row row : sheet){
	               if (rowIndex ==0){
	                   rowIndex++;
	                   continue;
	               }
	               if (row == null) continue; 
	               Iterator<Cell> cellIterator = row.iterator();
	               int cellIndex = 0;
	               CreditData creditData = new CreditData();
	               while (cellIterator.hasNext()){
	                   Cell cell = cellIterator.next();
	                   if(cell!=null) {
	                   switch (cellIndex){
	                  
	                    case 1 -> creditData.setRegdNo(getCellValueAsString(cell));
	                    case 2 -> creditData.setName(getCellValueAsString(cell));
	                    case 3 -> creditData.setSem(getCellValueAsString(cell));
	                    case 4 -> creditData.setSubjectCode(getCellValueAsString(cell));
	                    case 5 -> creditData.setSubjectName(getCellValueAsString(cell));
	                    case 6 -> creditData.setType(getCellValueAsString(cell));
	                    case 7 -> creditData.setCredits(getCellValueAsString(cell));
	                    case 8 -> creditData.setGrade(getCellValueAsString(cell));
	                    default -> {}
	                   }
	                   cellIndex++;
	               }
	               }
	               creditDatas.add(creditData);
	           }
	       } catch (IOException e) {
	           e.getStackTrace();
	       }
	       return creditDatas;
	   }
	  
	  private static String getCellValueAsString(Cell cell) {
		    switch (cell.getCellType()) {
		        case STRING:
		            return cell.getStringCellValue();
		        case NUMERIC:
		            if (DateUtil.isCellDateFormatted(cell)) {
		                // Handle date formatted cells if necessary
		                return cell.getDateCellValue().toString();
		            } else {
		                // Handle numeric cells
		                return String.valueOf((int) cell.getNumericCellValue());
		            }
		        case BOOLEAN:
		            return String.valueOf(cell.getBooleanCellValue());
		        case FORMULA:
		            return cell.getCellFormula();
		        default:
		            return "";
		    }
	  }
	  
	  public static List<SubjectData> getSubjectDataFromExcel(InputStream inputStream){
	        List<SubjectData> subjectDatas = new ArrayList<>();
	       try {
	           XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
	           XSSFSheet sheet = workbook.getSheet("work");
	           int rowIndex =0;
	           for (Row row : sheet){
	               if (rowIndex ==0){
	                   rowIndex++;
	                   continue;
	               }
	               Iterator<Cell> cellIterator = row.iterator();
	               int cellIndex = 0;
	               SubjectData creditData = new SubjectData();
	               while (cellIterator.hasNext()){
	                   Cell cell = cellIterator.next();
	                   switch (cellIndex){
	                    
	                       case 0 -> creditData.setProgramme(cell.getStringCellValue());
	                       case 1 -> creditData.setBranch(cell.getStringCellValue());
	                       case 2 -> creditData.setBasket(cell.getStringCellValue());
	                       case 3 -> creditData.setCourseType(cell.getStringCellValue());
	                       case 4 -> creditData.setCourseCode(cell.getStringCellValue());
	                       case 5 -> creditData.setSubjectName(cell.getStringCellValue().toUpperCase());
	                       case 6 -> creditData.setCredit((int)cell.getNumericCellValue());
	                       case 7 -> creditData.setType(cell.getStringCellValue());
	                      
	                       default -> {
	                       }
	                   }
	                   cellIndex++;
	               }
	               subjectDatas.add(creditData);
	           }
	       } catch (IOException e) {
	           e.getStackTrace();
	       }
	       return subjectDatas;
	   }
	  

	 
	    public List<CreditData> getAllDetailsByRegdNo(String regdNo) {
	        logger.info("Fetching all details for registration number: {}", regdNo);
	        List<CreditData> creditDataList;
	        try {
	            creditDataList = creditRepo.findByRegdNo(regdNo);
	        } catch (Exception e) {
	            logger.error("Error retrieving credit data for registration number: {}", regdNo, e);
	            throw new RuntimeException("Error retrieving credit data", e);
	        }
	        if (creditDataList.isEmpty()) {
	            logger.error("No data found for registration number: {}", regdNo);
	            throw new ResourceNotFoundException("No data found for registration number: " + regdNo);
	        }
	        return creditDataList;
	    }
	    
	    
	    public Map<String, String> getSubjectBasketByRegdNo(String regdNo) {
	        logger.info("Fetching subject names and baskets for registration number: {}", regdNo);
	        List<CreditData> creditDataList = getAllDetailsByRegdNo(regdNo);

	        // Normalize and filter out subjects with grades 'S' and 'F'
	        List<String> validSubjectNames = creditDataList.stream()
	                .filter(creditData -> !"S".equals(creditData.getGrade()) && !"F".equals(creditData.getGrade()))
	                .map(creditData -> creditData.getSubjectName().toUpperCase().trim()) // Normalize subject name
	                .distinct()
	                .collect(Collectors.toList());

	        // Fetch basket details from SubjectRepo
	        Map<String, String> subjectBasketMap = validSubjectNames.stream()
	                .map(subjectName -> {
	                    // Fetch all subjects from SubjectRepo
	                    List<SubjectData> subjects = subjectRepo.findAll(); // Adjust if you have a method to fetch all subjects
	                    return subjects.stream()
	                            .filter(subjectData -> subjectName.equalsIgnoreCase(subjectData.getSubjectName().trim()))
	                            .findFirst()
	                            .map(subjectData -> Map.entry(subjectData.getSubjectName(), subjectData.getBasket()))
	                            .orElse(null);
	                })
	                .filter(entry -> entry != null) // Filter out null entries
	                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

	        return subjectBasketMap;
	    }
	 

	    public List<Map<String, Object>> getSubjectBasketAndCreditsByRegdNo(String regdNo) {
	        logger.info("Fetching subject names, baskets, and credits for registration number: {}", regdNo);
	        List<CreditData> creditDataList = getAllDetailsByRegdNo(regdNo);

	        // Normalize and filter out subjects with grades 'S' and 'F'
	        List<String> validSubjectNames = creditDataList.stream()
	                .filter(creditData -> !"S".equals(creditData.getGrade()) && !"F".equals(creditData.getGrade()))
	                .map(creditData -> creditData.getSubjectName().toUpperCase().trim()) // Normalize subject name
	                .distinct()
	                .collect(Collectors.toList());

	        // Fetch basket details and credits from SubjectRepo
	        List<Map<String, Object>> subjectDetailsList = validSubjectNames.stream()
	                .map(subjectName -> {
	                    // Fetch all subjects from SubjectRepo
	                    List<SubjectData> subjects = subjectRepo.findAll(); // Adjust if you have a method to fetch all subjects
	                    return subjects.stream()
	                            .filter(subjectData -> subjectName.equalsIgnoreCase(subjectData.getSubjectName().trim()))
	                            .findFirst()
	                            .map(subjectData -> {
	                                Map<String, Object> subjectDetails = new HashMap<>();
	                                subjectDetails.put("Subject", subjectData.getSubjectName());
	                                subjectDetails.put("Basket", subjectData.getBasket());
	                                subjectDetails.put("Credits", (subjectData.getCredit()));
	                                return subjectDetails;
	                            })
	                            .orElse(null);
	                })
	                .filter(details -> details != null) // Filter out null entries
	                .collect(Collectors.toList());

	        return subjectDetailsList;
	    }

	    private static final Map<String, Integer> TOTAL_CREDITS = Map.of(
	            "Basket I", 17,
	            "Basket II", 12,
	            "Basket III", 26,
	            "Basket IV", 58,
	            "Basket V", 48
	        );

	        public List<Map<String, Object>> getBasketCredits(String regdNo) {
	            List<Map<String, Object>> subjectDetailsList = getSubjectBasketAndCreditsByRegdNo(regdNo);
	       if(subjectDetailsList.isEmpty()) {
	    	   throw new ResourceNotFoundException("No data found for registration number: " + regdNo);
	    	   
	       }

	            // Initialize credits tracking
	            Map<String, Integer> basketCredits = new HashMap<>();
	            TOTAL_CREDITS.keySet().forEach(basket -> basketCredits.put(basket, 0));

	            // Aggregate credits for each basket
	            for (Map<String, Object> details : subjectDetailsList) {
	                String basket = (String) details.get("Basket");
	                Integer credits = (Integer) details.get("Credits");
	                if (TOTAL_CREDITS.containsKey(basket)) {
	                    basketCredits.put(basket, basketCredits.get(basket) + credits);
	                }
	            }

	            // Prepare result list
	            List<Map<String, Object>> result = new ArrayList<>();
	            for (Map.Entry<String, Integer> entry : TOTAL_CREDITS.entrySet()) {
	                String basket = entry.getKey();
	                int totalCredits = entry.getValue();
	                int completedCredits = basketCredits.getOrDefault(basket, 0);
	                int pendingCredits = totalCredits - completedCredits;
	                if(pendingCredits <0) {
	                	pendingCredits= 0;
	                }

	                Map<String, Object> basketInfo = new HashMap<>();
	                basketInfo.put("Basket", basket);
	                basketInfo.put("Completed", completedCredits);
	                basketInfo.put("Pending", pendingCredits);

	                result.add(basketInfo);
	            }

	            return result;
	        }
	  
	        public SubjectDataCSE convertToSubjectDataCSE(SubjectData subjectData) {
	            return new SubjectDataCSE(
	                subjectData.getSno(),
	                subjectData.getProgramme(),
	                subjectData.getBranch(),
	                subjectData.getBasket(),
	                subjectData.getCourseType(),
	                subjectData.getCourseCode(),
	                subjectData.getSubjectName(),
	                subjectData.getCredit(),
	                subjectData.getType()
	            );
	        }

	        // Similarly, create methods for other branches like convertToSubjectDataECE(), convertToSubjectDataEEE(), etc.

	        public SubjectDataEEE convertToSubjectDataEEE(SubjectData subjectData) {
	            return new SubjectDataEEE(
	                subjectData.getSno(),
	                subjectData.getProgramme(),
	                subjectData.getBranch(),
	                subjectData.getBasket(),
	                subjectData.getCourseType(),
	                subjectData.getCourseCode(),
	                subjectData.getSubjectName(),
	                subjectData.getCredit(),
	                subjectData.getType()
	            );
	        }
	        
	        
	        public SubjectDataECE convertToSubjectDataECE(SubjectData subjectData) {
	            return new SubjectDataECE(
	                subjectData.getSno(),
	                subjectData.getProgramme(),
	                subjectData.getBranch(),
	                subjectData.getBasket(),
	                subjectData.getCourseType(),
	                subjectData.getCourseCode(),
	                subjectData.getSubjectName(),
	                subjectData.getCredit(),
	                subjectData.getType()
	            );
	        }

	    

	        
	        public SubjectDataMECH convertToSubjectDataMECH(SubjectData subjectData) {
	            return new SubjectDataMECH(
	                subjectData.getSno(),
	                subjectData.getProgramme(),
	                subjectData.getBranch(),
	                subjectData.getBasket(),
	                subjectData.getCourseType(),
	                subjectData.getCourseCode(),
	                subjectData.getSubjectName(),
	                subjectData.getCredit(),
	                subjectData.getType()
	            );
	        }

	        // Similarly, create methods for other branches like convertToSubjectDataECE(), convertToSubjectDataEEE(), etc.

	        // Similarly, create methods for other branches like convertToSubjectDataECE(), convertToSubjectDataEEE(), etc.
	        public SubjectDataCIVIL convertToSubjectDataCIVIL(SubjectData subjectData) {
	            return new SubjectDataCIVIL(
	                subjectData.getSno(),
	                subjectData.getProgramme(),
	                subjectData.getBranch(),
	                subjectData.getBasket(),
	                subjectData.getCourseType(),
	                subjectData.getCourseCode(),
	                subjectData.getSubjectName(),
	                subjectData.getCredit(),
	                subjectData.getType()
	            );
	        }

	        // Similarly, create methods for other branches like convertToSubjectDataECE(), convertToSubjectDataEEE(), etc.
	        public List<Map<String, Object>> getSubjectBasketAndCreditsByRegdNo(String regdNo, String branch) {
	            logger.info("Fetching subject names, baskets, and credits for registration number: {} and branch: {}", regdNo, branch);

	            List<CreditData> creditDataList = getAllDetailsByRegdNo(regdNo);

	            List<String> validSubjectNames = creditDataList.stream()
	                    .filter(creditData -> !"S".equals(creditData.getGrade()) && !"F".equals(creditData.getGrade()))
	                    .map(creditData -> creditData.getSubjectName().toUpperCase().trim())
	                    .distinct()
	                    .collect(Collectors.toList());

	            // Determine which repository to use based on the branch
	            List<SubjectData> subjects;
	            switch (branch.toUpperCase()) {
	                case "CSE":
	                  subjects = (List<SubjectData>) (List<?>) subjectRepoCSE.findAll();
	                    break;
	                case "ECE":
	                    subjects =(List<SubjectData>) (List<?>) subjectRepoECE.findAll();
	                    break;
	                case "EEE":
	                    subjects = (List<SubjectData>) (List<?>)subjectRepoEEE.findAll();
	                    break;
	                case "MECH":
	                    subjects = (List<SubjectData>) (List<?>)subjectRepoMECH.findAll();
	                    break;
	                case "CIVIL":
	                    subjects = (List<SubjectData>) (List<?>)subjectRepoCIVIL.findAll();
	                    break;
	                default:
	                    throw new IllegalArgumentException("Invalid branch: " + branch);
	            }

	            List<Map<String, Object>> subjectDetailsList = validSubjectNames.stream()
	                    .map(subjectName -> subjects.stream()
	                            .filter(subjectData -> subjectName.equalsIgnoreCase(subjectData.getSubjectName().trim()))
	                            .findFirst()
	                            .map(subjectData -> {
	                                Map<String, Object> subjectDetails = new HashMap<>();
	                                subjectDetails.put("Subject", subjectData.getSubjectName());
	                                subjectDetails.put("Basket", subjectData.getBasket());
	                                subjectDetails.put("Credits", subjectData.getCredit());
	                                return subjectDetails;
	                            })
	                            .orElse(null))
	                    .filter(details -> details != null)
	                    .collect(Collectors.toList());

	            return subjectDetailsList;
	        } 

	        public List<Map<String, Object>> getBasketCredits(String regdNo, String branch) {
	            List<Map<String, Object>> subjectDetailsList = getSubjectBasketAndCreditsByRegdNo(regdNo, branch);
	            if (subjectDetailsList.isEmpty()) {
	                throw new ResourceNotFoundException("No data found for registration number: " + regdNo + " and branch: " + branch);
	            }

	            // Initialize credits tracking
	            Map<String, Integer> basketCredits = new HashMap<>();
	            TOTAL_CREDITS.keySet().forEach(basket -> basketCredits.put(basket, 0));

	            // Aggregate credits for each basket
	            for (Map<String, Object> details : subjectDetailsList) {
	                String basket = (String) details.get("Basket");
	                Integer credits = (Integer) details.get("Credits");
	                if (TOTAL_CREDITS.containsKey(basket)) {
	                    basketCredits.put(basket, basketCredits.get(basket) + credits);
	                }
	            }

	            // Prepare result list
	            List<Map<String, Object>> result = new ArrayList<>();
	            for (Map.Entry<String, Integer> entry : TOTAL_CREDITS.entrySet()) {
	                String basket = entry.getKey();
	                int totalCredits = entry.getValue();
	                int completedCredits = basketCredits.getOrDefault(basket, 0);
	                int pendingCredits = totalCredits - completedCredits;
	                if (pendingCredits < 0) {
	                    pendingCredits = 0;
	                }

	                Map<String, Object> basketInfo = new HashMap<>();
	                basketInfo.put("Basket", basket);
	                basketInfo.put("Completed", completedCredits);
	                basketInfo.put("Pending", pendingCredits);

	                result.add(basketInfo);
	            }

	            return result;
	        }
	        
	        
	        
	       

	        public void saveStudentsToDatabase(MultipartFile file, int year) {
	            try {
	                List<CreditData> creditDatas = getStudentsDataFromExcel(file.getInputStream());
	                switch (year) {
	                    case 1:
	                        List<CreditData1stYear> creditData1stYearList = mapToCreditData1stYear(creditDatas);
	                        creditData1stYearRepo.saveAll(creditData1stYearList);
	                        break;
	                    case 2:
	                        List<CreditData2ndYear> creditData2ndYearList = mapToCreditData2ndYear(creditDatas);
	                        creditData2ndYearRepo.saveAll(creditData2ndYearList);
	                        break;
	                    case 3:
	                        List<CreditData3rdYear> creditData3rdYearList = mapToCreditData3rdYear(creditDatas);
	                        creditData3rdYearRepo.saveAll(creditData3rdYearList);
	                        break;
	                    case 4:
	                        List<CreditData4thYear> creditData4thYearList = mapToCreditData4thYear(creditDatas);
	                        creditData4thYearRepo.saveAll(creditData4thYearList);
	                        break;
	                    default:
	                        throw new IllegalArgumentException("Invalid year value. It must be one of 1, 2, 3, or 4.");
	                }
	            } catch (IOException e) {
	                throw new IllegalArgumentException("The file is not a valid Excel file", e);
	            }
	        }

	        private List<CreditData1stYear> mapToCreditData1stYear(List<CreditData> creditDatas) {
	            List<CreditData1stYear> creditData1stYearList = new ArrayList<>();
	            for (CreditData creditData : creditDatas) {
	                CreditData1stYear creditData1stYear = new CreditData1stYear();
	                creditData1stYear.setRegdNo(creditData.getRegdNo());
	                creditData1stYear.setName(creditData.getName());
	                creditData1stYear.setSem(creditData.getSem());
	                creditData1stYear.setSubjectCode(creditData.getSubjectCode());
	                creditData1stYear.setSubjectName(creditData.getSubjectName());
	                creditData1stYear.setType(creditData.getType());
	                creditData1stYear.setCredits(creditData.getCredits());
	                creditData1stYear.setGrade(creditData.getGrade());
	                creditData1stYearList.add(creditData1stYear);
	            }
	            return creditData1stYearList;
	        }

	        private List<CreditData2ndYear> mapToCreditData2ndYear(List<CreditData> creditDatas) {
	            List<CreditData2ndYear> creditData2ndYearList = new ArrayList<>();
	            for (CreditData creditData : creditDatas) {
	                CreditData2ndYear creditData2ndYear = new CreditData2ndYear();
	                creditData2ndYear.setRegdNo(creditData.getRegdNo());
	                creditData2ndYear.setName(creditData.getName());
	                creditData2ndYear.setSem(creditData.getSem());
	                creditData2ndYear.setSubjectCode(creditData.getSubjectCode());
	                creditData2ndYear.setSubjectName(creditData.getSubjectName());
	                creditData2ndYear.setType(creditData.getType());
	                creditData2ndYear.setCredits(creditData.getCredits());
	                creditData2ndYear.setGrade(creditData.getGrade());
	                creditData2ndYearList.add(creditData2ndYear);
	            }
	            return creditData2ndYearList;
	        }

	        private List<CreditData3rdYear> mapToCreditData3rdYear(List<CreditData> creditDatas) {
	            List<CreditData3rdYear> creditData3rdYearList = new ArrayList<>();
	            for (CreditData creditData : creditDatas) {
	                CreditData3rdYear creditData3rdYear = new CreditData3rdYear();
	                creditData3rdYear.setRegdNo(creditData.getRegdNo());
	                creditData3rdYear.setName(creditData.getName());
	                creditData3rdYear.setSem(creditData.getSem());
	                creditData3rdYear.setSubjectCode(creditData.getSubjectCode());
	                creditData3rdYear.setSubjectName(creditData.getSubjectName());
	                creditData3rdYear.setType(creditData.getType());
	                creditData3rdYear.setCredits(creditData.getCredits());
	                creditData3rdYear.setGrade(creditData.getGrade());
	                creditData3rdYearList.add(creditData3rdYear);
	            }
	            return creditData3rdYearList;
	        }

	        private List<CreditData4thYear> mapToCreditData4thYear(List<CreditData> creditDatas) {
	            List<CreditData4thYear> creditData4thYearList = new ArrayList<>();
	            for (CreditData creditData : creditDatas) {
	                CreditData4thYear creditData4thYear = new CreditData4thYear();
	                creditData4thYear.setRegdNo(creditData.getRegdNo());
	                creditData4thYear.setName(creditData.getName());
	                creditData4thYear.setSem(creditData.getSem());
	                creditData4thYear.setSubjectCode(creditData.getSubjectCode());
	                creditData4thYear.setSubjectName(creditData.getSubjectName());
	                creditData4thYear.setType(creditData.getType());
	                creditData4thYear.setCredits(creditData.getCredits());
	                creditData4thYear.setGrade(creditData.getGrade());
	                creditData4thYearList.add(creditData4thYear);
	            }
	            return creditData4thYearList;
	        }

	        public List<?> getAllDetailsByRegdNo(String regdNo, int year) {
	            logger.info("Fetching all details for registration number: {} for year {}", regdNo, year);
	            List<?> creditDataList;
	            try {
	                switch (year) {
	                    case 1:
	                        creditDataList = creditData1stYearRepo.findByRegdNo(regdNo);
	                        break;
	                    case 2:
	                        creditDataList = creditData2ndYearRepo.findByRegdNo(regdNo);
	                        break;
	                    case 3:
	                        creditDataList = creditData3rdYearRepo.findByRegdNo(regdNo);
	                        break;
	                    case 4:
	                        creditDataList = creditData4thYearRepo.findByRegdNo(regdNo);
	                        break;
	                    default:
	                        throw new IllegalArgumentException("Invalid year value. It must be one of 1, 2, 3, or 4.");
	                }
	            } catch (Exception e) {
	                logger.error("Error retrieving credit data for registration number: {} for year {}", regdNo, year, e);
	                throw new RuntimeException("Error retrieving credit data", e);
	            }
	            if (creditDataList.isEmpty()) {
	                logger.error("No data found for registration number: {} for year {}", regdNo, year);
	                throw new ResourceNotFoundException("No data found for registration number: " + regdNo + " for year " + year);
	            }
	            return creditDataList;
	        }

	        
	        public String getHighestSemesterByRegdNo(String regdNo, int year) {
	            logger.info("Fetching highest semester for registration number: {} and year {}", regdNo, year);
	            String highestSemester;
	            try {
	                switch (year) {
	                    case 1:
	                        highestSemester = creditData1stYearRepo.findHighestSemesterByRegdNo(regdNo);
	                        break;
	                    case 2:
	                        highestSemester = creditData2ndYearRepo.findHighestSemesterByRegdNo(regdNo);
	                        break;
	                    case 3:
	                        highestSemester = creditData3rdYearRepo.findHighestSemesterByRegdNo(regdNo);
	                        break;
	                    case 4:
	                        highestSemester = creditData4thYearRepo.findHighestSemesterByRegdNo(regdNo);
	                        break;
	                    default:
	                        throw new IllegalArgumentException("Invalid year value. It must be one of 1, 2, 3, or 4.");
	                }
	                if (highestSemester == null) {
	                    logger.error("No semester data found for registration number: {} and year {}", regdNo, year);
	                    throw new ResourceNotFoundException("No semester data found for registration number: " + regdNo + " and year " + year);
	                }
	            } catch (Exception e) {
	                logger.error("Error retrieving highest semester for registration number: {} and year {}", regdNo, year, e);
	                throw new RuntimeException("Error retrieving highest semester", e);
	            }
	            return highestSemester;
	        }
	        public boolean deleteStudentData(int year) {
	            try {
	                switch (year) {
	                    case 1:
	                        creditData1stYearRepo.deleteAll();
	                        break;
	                    case 2:
	                        creditData2ndYearRepo.deleteAll();
	                        break;
	                    case 3:
	                        creditData3rdYearRepo.deleteAll();
	                        break;
	                    case 4:
	                        creditData4thYearRepo.deleteAll();
	                        break;
	                    default:
	                        throw new IllegalArgumentException("Invalid year value. It must be one of 1, 2, 3, or 4.");
	                }
	                return true;
	            } catch (Exception e) {
	                logger.error("Error deleting student data for year {}: {}", year, e.getMessage());
	                return false;
	            }
	        }
	        
	        public List<Map<String, Object>> getSubjectBasketAndCreditsByRegdNo(String regdNo, String branch, int year) {
	            logger.info("Fetching subject names, baskets, and credits for registration number: {} branch: {}, and year: {}", regdNo, branch, year);

	            List<CreditData> creditDataList = (List<CreditData>) getAllDetailsByRegdNo(regdNo, year);

	            List<String> validSubjectNames = creditDataList.stream()
	                    .filter(creditData -> !"S".equals(creditData.getGrade()) && !"F".equals(creditData.getGrade()))
	                    .map(creditData -> creditData.getSubjectName().toUpperCase().trim())
	                    .distinct()
	                    .collect(Collectors.toList());

	            // Determine which repository to use based on the branch
	            List<SubjectData> subjects;
	            switch (branch.toUpperCase()) {
	                case "CSE":
	                    subjects = (List<SubjectData>) (List<?>) subjectRepoCSE.findAll();
	                    break;
	                case "ECE":
	                    subjects = (List<SubjectData>) (List<?>) subjectRepoECE.findAll();
	                    break;
	                case "EEE":
	                    subjects = (List<SubjectData>) (List<?>) subjectRepoEEE.findAll();
	                    break;
	                case "MECH":
	                    subjects = (List<SubjectData>) (List<?>) subjectRepoMECH.findAll();
	                    break;
	                case "CIVIL":
	                    subjects = (List<SubjectData>) (List<?>) subjectRepoCIVIL.findAll();
	                    break;
	                default:
	                    throw new IllegalArgumentException("Invalid branch: " + branch);
	            }

	            List<Map<String, Object>> subjectDetailsList = validSubjectNames.stream()
	                    .map(subjectName -> subjects.stream()
	                            .filter(subjectData -> subjectName.equalsIgnoreCase(subjectData.getSubjectName().trim()))
	                            .findFirst()
	                            .map(subjectData -> {
	                                Map<String, Object> subjectDetails = new HashMap<>();
	                                subjectDetails.put("Subject", subjectData.getSubjectName());
	                                subjectDetails.put("Basket", subjectData.getBasket());
	                                subjectDetails.put("Credits", subjectData.getCredit());
	                                return subjectDetails;
	                            })
	                            .orElse(null))
	                    .filter(details -> details != null)
	                    .collect(Collectors.toList());

	            return subjectDetailsList;
	        }

	        public List<Map<String, Object>> getBasketCredits(String regdNo, String branch, int year) {
	            List<Map<String, Object>> subjectDetailsList = getSubjectBasketAndCreditsByRegdNo(regdNo, branch, year);
	            if (subjectDetailsList.isEmpty()) {
	                throw new ResourceNotFoundException("No data found for registration number: " + regdNo + " branch: " + branch + " and year: " + year);
	            }

	            // Initialize credits tracking
	            Map<String, Integer> basketCredits = new HashMap<>();
	            TOTAL_CREDITS.keySet().forEach(basket -> basketCredits.put(basket, 0));

	            // Aggregate credits for each basket
	            for (Map<String, Object> details : subjectDetailsList) {
	                String basket = (String) details.get("Basket");
	                Integer credits = (Integer) details.get("Credits");
	                if (TOTAL_CREDITS.containsKey(basket)) {
	                    basketCredits.put(basket, basketCredits.get(basket) + credits);
	                }
	            }

	            // Prepare result list
	            List<Map<String, Object>> result = new ArrayList<>();
	            for (Map.Entry<String, Integer> entry : TOTAL_CREDITS.entrySet()) {
	                String basket = entry.getKey();
	                int totalCredits = entry.getValue();
	                int completedCredits = basketCredits.getOrDefault(basket, 0);
	                int pendingCredits = totalCredits - completedCredits;
	                if (pendingCredits < 0) {
	                    pendingCredits = 0;
	                }

	                Map<String, Object> basketInfo = new HashMap<>();
	                basketInfo.put("Basket", basket);
	                basketInfo.put("Completed", completedCredits);
	                basketInfo.put("Pending", pendingCredits);

	                result.add(basketInfo);
	            }

	            return result;
	        }



			public void saveSubjectsToDatabaseForm(String branch, SubjectDataReq subjectData) {
				
				
				try {
	                switch (branch) {
	                    case "CSE":
	                    	
	                    	SubjectDataCSE subjectDataCSE = new SubjectDataCSE();
	                     subjectDataCSE.setProgramme(subjectData.getProgramme());
	                     subjectDataCSE.setBranch(branch);
	                     subjectDataCSE.setBasket(subjectData.getBasket());
	                     subjectDataCSE.setCourseType(subjectData.getCourseType());
	                     subjectDataCSE.setCourseCode(subjectData.getCourseCode());
	                     subjectDataCSE.setSubjectName(subjectData.getSubjectName().toUpperCase());
	                     subjectDataCSE.setCredit(subjectData.getCredit());
	                     subjectDataCSE.setType(subjectData.getType());
	                     subjectRepoCSE.save(subjectDataCSE);
	                        break;
	                    case "ECE":
	                    	
	                    	SubjectDataECE subjectDataECE = new SubjectDataECE();
	                    	subjectDataECE.setProgramme(subjectData.getProgramme());
	                    	subjectDataECE.setBranch(branch);
	                    	subjectDataECE.setBasket(subjectData.getBasket());
	                    	subjectDataECE.setCourseType(subjectData.getCourseType());
	                    	subjectDataECE.setCourseCode(subjectData.getCourseCode());
	                    	subjectDataECE.setSubjectName(subjectData.getSubjectName().toUpperCase());
	                    	subjectDataECE.setCredit(subjectData.getCredit());
	                    	subjectDataECE.setType(subjectData.getType());
	                    	subjectRepoECE.save(subjectDataECE);
	                        break;
	                    case "EEE":
	                    	SubjectDataEEE subjectDataEEE = new SubjectDataEEE();
	                    	subjectDataEEE.setProgramme(subjectData.getProgramme());
	                    	subjectDataEEE.setBranch(branch);
	                    	subjectDataEEE.setBasket(subjectData.getBasket());
	                    	subjectDataEEE.setCourseType(subjectData.getCourseType());
	                    	subjectDataEEE.setCourseCode(subjectData.getCourseCode());
	                    	subjectDataEEE.setSubjectName(subjectData.getSubjectName().toUpperCase());
	                    	subjectDataEEE.setCredit(subjectData.getCredit());
	                    	subjectDataEEE.setType(subjectData.getType());
	                    	subjectRepoEEE.save(subjectDataEEE);
	                        
	                        break;
	                    case "MECH":
	                    	SubjectDataMECH subjectDataMECH = new SubjectDataMECH();
	                    	subjectDataMECH.setProgramme(subjectData.getProgramme());
	                    	subjectDataMECH.setBranch(branch);
	                    	subjectDataMECH.setBasket(subjectData.getBasket());
	                    	subjectDataMECH.setCourseType(subjectData.getCourseType());
	                    	subjectDataMECH.setCourseCode(subjectData.getCourseCode());
	                    	subjectDataMECH.setSubjectName(subjectData.getSubjectName().toUpperCase());
	                    	subjectDataMECH.setCredit(subjectData.getCredit());
	                    	subjectDataMECH.setType(subjectData.getType());
	                    	subjectRepoMECH.save(subjectDataMECH);
	                        
	                        break;
	                    case "CIVIL":
	                    	SubjectDataCIVIL subjectDataCIVIL = new SubjectDataCIVIL();
	                    	subjectDataCIVIL.setProgramme(subjectData.getProgramme());
	                    	subjectDataCIVIL.setBranch(branch);
	                    	subjectDataCIVIL.setBasket(subjectData.getBasket());
	                    	subjectDataCIVIL.setCourseType(subjectData.getCourseType());
	                    	subjectDataCIVIL.setCourseCode(subjectData.getCourseCode());
	                    	subjectDataCIVIL.setSubjectName(subjectData.getSubjectName().toUpperCase());
	                    	subjectDataCIVIL.setCredit(subjectData.getCredit());
	                    	subjectDataCIVIL.setType(subjectData.getType());
	                    	subjectRepoCIVIL.save(subjectDataCIVIL);
	                        break;
	                    default:
	                        throw new IllegalArgumentException("Invalid branch . ");
	                }
	              
	            } catch (Exception e) {
	                logger.error("Error saving subject data for branch {}: {}",branch, e.getMessage());
	               
	            }
				
				
			}




}