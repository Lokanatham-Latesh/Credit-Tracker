package com.Tracker.CreditRepositrory;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Tracker.Entity.CreditData;

public interface CreditRepo extends JpaRepository<CreditData, Integer>{
	   List<CreditData> findByRegdNo(String regdNo);
	

}
