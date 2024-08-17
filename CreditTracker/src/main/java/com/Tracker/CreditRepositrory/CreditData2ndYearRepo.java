package com.Tracker.CreditRepositrory;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.Tracker.Entity.CreditData1stYear;
import com.Tracker.Entity.CreditData2ndYear;

import jakarta.transaction.Transactional;
@Transactional
public interface CreditData2ndYearRepo extends JpaRepository<CreditData2ndYear, Integer> {

	List<?> findByRegdNo(String regdNo);

	List<CreditData2ndYear> findAllByRegdNo(String regdNo);

	@Modifying
    @Query("DELETE FROM CreditData1stYear c WHERE c.sem = :sem")
    void deleteBySem(@Param("sem") String sem);
	
	 @Modifying
	    @Query(value = "TRUNCATE TABLE credit_data2nd_year", nativeQuery = true)
	    void truncateTable();
	 
	   @Query("SELECT MAX(c.sem) FROM CreditData2ndYear c WHERE c.regdNo = :regdNo")
	    String findHighestSemesterByRegdNo(@Param("regdNo") String regdNo);

}
