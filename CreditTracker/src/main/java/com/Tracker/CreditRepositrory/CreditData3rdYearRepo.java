package com.Tracker.CreditRepositrory;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.Tracker.Entity.CreditData3rdYear;

import jakarta.transaction.Transactional;
@Transactional
public interface CreditData3rdYearRepo extends JpaRepository<CreditData3rdYear, Integer> {

	List<?> findByRegdNo(String regdNo);

	List<CreditData3rdYear> findAllByRegdNo(String regdNo);

	@Modifying
    @Query("DELETE FROM CreditData1stYear c WHERE c.sem = :sem")
    void deleteBySem(@Param("sem") String sem);
	
	 @Modifying
	    @Query(value = "TRUNCATE TABLE credit_data3rd_year", nativeQuery = true)
	    void truncateTable();
	   @Query("SELECT MAX(c.sem) FROM CreditData3rdYear c WHERE c.regdNo = :regdNo")
	    String findHighestSemesterByRegdNo(@Param("regdNo") String regdNo);
	   
	   
	 
}
