package com.Tracker.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Tracker.CreditRepositrory.CreditData2ndYearRepo;
import com.Tracker.CreditRepositrory.SubjectRepoCIVIL;
import com.Tracker.CreditRepositrory.SubjectRepoCSE;
import com.Tracker.CreditRepositrory.SubjectRepoECE;
import com.Tracker.CreditRepositrory.SubjectRepoEEE;
import com.Tracker.CreditRepositrory.SubjectRepoMECH;
import com.Tracker.Entity.CreditData1stYear;
import com.Tracker.Entity.CreditData2ndYear;
import com.Tracker.Entity.SubjectDataCIVIL;
import com.Tracker.Entity.SubjectDataCSE;
import com.Tracker.Entity.SubjectDataECE;
import com.Tracker.Entity.SubjectDataEEE;
import com.Tracker.Entity.SubjectDataMECH;

@Service
public class CreditTrackStudents2ndYear {
	
@Autowired
private  CreditData2ndYearRepo creditData2ndYearRepo;

@Autowired
private SubjectRepoCIVIL subjectRepoCIVIL;

@Autowired
private SubjectRepoCSE subjectRepoCSE;

@Autowired
private SubjectRepoECE subjectRepoECE;

@Autowired
private SubjectRepoEEE subjectRepoEEE;

@Autowired
private SubjectRepoMECH subjectRepoMECH;



private static final Map<String, Integer> TOTAL_CREDITS = Map.of(
        "Basket I", 17,
        "Basket II", 12,
        "Basket III", 25,
        "Basket IV", 58,
        "Basket V", 48
);


public List<Map<String, Object>> getCSE2ndYearBasketCreditsByRegdNo(String regdNo) {
    // Get the filtered credit data
    List<CreditData2ndYear> creditDataList = get2ndYearCreditDataByRegdNo(regdNo);
    if (creditDataList.isEmpty()) {
        // Return a list with a message indicating no data was found
        Map<String, Object> message = new HashMap<>();
        message.put("Message", "No credit data found for the given registration number.");
        return Collections.singletonList(message);
    }



    // Normalize and filter out subjects with grades 'S' and 'F'
    List<String> validSubjectNames = creditDataList.stream()
            .filter(creditData -> !"S".equals(creditData.getGrade()) && !"F".equals(creditData.getGrade()))
            .map(creditData -> creditData.getSubjectName().toUpperCase().trim()) // Normalize subject name
            .distinct()
            .collect(Collectors.toList());

    // Fetch basket details and credits from SubjectRepoCSE
    List<SubjectDataCSE> subjects = subjectRepoCSE.findAll();

    // Map subject names to their baskets and credits
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
            .filter(details -> details != null) // Filter out null entries
            .collect(Collectors.toList());

    // Calculate the basket credits
    Map<String, Integer> basketCredits = new HashMap<>();
    TOTAL_CREDITS.keySet().forEach(basket -> basketCredits.put(basket, 0));

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

public List<Map<String, Object>> getECE2ndYearBasketCreditsByRegdNo(String regdNo) {
    // Get the filtered credit data
    List<CreditData2ndYear> creditDataList = get2ndYearCreditDataByRegdNo(regdNo);
    if (creditDataList.isEmpty()) {
        // Return a list with a message indicating no data was found
        Map<String, Object> message = new HashMap<>();
        message.put("Message", "No credit data found for the given registration number.");
        return Collections.singletonList(message);
    }



    // Normalize and filter out subjects with grades 'S' and 'F'
    List<String> validSubjectNames = creditDataList.stream()
            .filter(creditData -> !"S".equals(creditData.getGrade()) && !"F".equals(creditData.getGrade()))
            .map(creditData -> creditData.getSubjectName().toUpperCase().trim()) // Normalize subject name
            .distinct()
            .collect(Collectors.toList());

    // Fetch basket details and credits from SubjectRepoCSE
    List<SubjectDataECE> subjects = subjectRepoECE.findAll();

    // Map subject names to their baskets and credits
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
            .filter(details -> details != null) // Filter out null entries
            .collect(Collectors.toList());

    // Calculate the basket credits
    Map<String, Integer> basketCredits = new HashMap<>();
    TOTAL_CREDITS.keySet().forEach(basket -> basketCredits.put(basket, 0));

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



public List<Map<String, Object>> getEEE2ndYearBasketCreditsByRegdNo(String regdNo) {
    // Get the filtered credit data
    List<CreditData2ndYear> creditDataList = get2ndYearCreditDataByRegdNo(regdNo);
    if (creditDataList.isEmpty()) {
        // Return a list with a message indicating no data was found
        Map<String, Object> message = new HashMap<>();
        message.put("Message", "No credit data found for the given registration number.");
        return Collections.singletonList(message);
    }



    // Normalize and filter out subjects with grades 'S' and 'F'
    List<String> validSubjectNames = creditDataList.stream()
            .filter(creditData -> !"S".equals(creditData.getGrade()) && !"F".equals(creditData.getGrade()))
            .map(creditData -> creditData.getSubjectName().toUpperCase().trim()) // Normalize subject name
            .distinct()
            .collect(Collectors.toList());

    // Fetch basket details and credits from SubjectRepoCSE
    List<SubjectDataEEE> subjects = subjectRepoEEE.findAll();

    // Map subject names to their baskets and credits
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
            .filter(details -> details != null) // Filter out null entries
            .collect(Collectors.toList());

    // Calculate the basket credits
    Map<String, Integer> basketCredits = new HashMap<>();
    TOTAL_CREDITS.keySet().forEach(basket -> basketCredits.put(basket, 0));

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



public List<Map<String, Object>> getMECH2ndYearBasketCreditsByRegdNo(String regdNo) {
    // Get the filtered credit data
    List<CreditData2ndYear> creditDataList = get2ndYearCreditDataByRegdNo(regdNo);
    if (creditDataList.isEmpty()) {
        // Return a list with a message indicating no data was found
        Map<String, Object> message = new HashMap<>();
        message.put("Message", "No credit data found for the given registration number.");
        return Collections.singletonList(message);
    }



    // Normalize and filter out subjects with grades 'S' and 'F'
    List<String> validSubjectNames = creditDataList.stream()
            .filter(creditData -> !"S".equals(creditData.getGrade()) && !"F".equals(creditData.getGrade()))
            .map(creditData -> creditData.getSubjectName().toUpperCase().trim()) // Normalize subject name
            .distinct()
            .collect(Collectors.toList());

    // Fetch basket details and credits from SubjectRepoCSE
    List<SubjectDataMECH> subjects = subjectRepoMECH.findAll();

    // Map subject names to their baskets and credits
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
            .filter(details -> details != null) // Filter out null entries
            .collect(Collectors.toList());

    // Calculate the basket credits
    Map<String, Integer> basketCredits = new HashMap<>();
    TOTAL_CREDITS.keySet().forEach(basket -> basketCredits.put(basket, 0));

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

public List<Map<String, Object>> getCIVIL2ndYearBasketCreditsByRegdNo(String regdNo) {
    // Get the filtered credit data
    List<CreditData2ndYear> creditDataList = get2ndYearCreditDataByRegdNo(regdNo);
    if (creditDataList.isEmpty()) {
        // Return a list with a message indicating no data was found
        Map<String, Object> message = new HashMap<>();
        message.put("Message", "No credit data found for the given registration number.");
        return Collections.singletonList(message);
    }



    // Normalize and filter out subjects with grades 'S' and 'F'
    List<String> validSubjectNames = creditDataList.stream()
            .filter(creditData -> !"S".equals(creditData.getGrade()) && !"F".equals(creditData.getGrade()))
            .map(creditData -> creditData.getSubjectName().toUpperCase().trim()) // Normalize subject name
            .distinct()
            .collect(Collectors.toList());

    // Fetch basket details and credits from SubjectRepoCSE
    List<SubjectDataCIVIL> subjects = subjectRepoCIVIL.findAll();

    // Map subject names to their baskets and credits
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
            .filter(details -> details != null) // Filter out null entries
            .collect(Collectors.toList());

    // Calculate the basket credits
    Map<String, Integer> basketCredits = new HashMap<>();
    TOTAL_CREDITS.keySet().forEach(basket -> basketCredits.put(basket, 0));

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



public List<CreditData2ndYear> get2ndYearCreditDataByRegdNo(String regdNo) {
    // Fetch data from CreditData1stYearRepo for the given registration number
    return creditData2ndYearRepo.findAllByRegdNo(regdNo);
}


	
}
