package com.Tracker.CreditRepositrory;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.Tracker.Entity.CreditData1stYear;

import jakarta.transaction.Transactional;
@Transactional
public interface CreditData1stYearRepo extends JpaRepository<CreditData1stYear, Integer>{

	List<?> findByRegdNo(String regdNo);

	List<CreditData1stYear> findAllByRegdNo(String regdNo);

	@Modifying
    @Query("DELETE FROM CreditData1stYear c WHERE c.sem = :sem")
    void deleteBySem(@Param("sem") String sem);
	
	 @Modifying
	    @Query(value = "TRUNCATE TABLE credit_data1st_year", nativeQuery = true)
	    void truncateTable();
	 
	   @Query("SELECT MAX(c.sem) FROM CreditData1stYear c WHERE c.regdNo = :regdNo")
	    String findHighestSemesterByRegdNo(@Param("regdNo") String regdNo);

}
