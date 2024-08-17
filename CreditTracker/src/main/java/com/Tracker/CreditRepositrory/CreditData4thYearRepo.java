package com.Tracker.CreditRepositrory;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.Tracker.Entity.CreditData4thYear;

import jakarta.transaction.Transactional;
@Transactional
public interface CreditData4thYearRepo extends JpaRepository<CreditData4thYear, Integer>{
  
	List<?> findByRegdNo(String regdNo);
	List<CreditData4thYear> findAllByRegdNo(String regdNo);
	@Modifying
    @Query("DELETE FROM CreditData1stYear c WHERE c.sem = :sem")
    void deleteBySem(@Param("sem") String sem);
	
	 @Modifying
	    @Query(value = "TRUNCATE TABLE credit_data4th_year", nativeQuery = true)
	    void truncateTable();

	   @Query("SELECT MAX(c.sem) FROM CreditData4thYear c WHERE c.regdNo = :regdNo")
	    String findHighestSemesterByRegdNo(@Param("regdNo") String regdNo);

}
