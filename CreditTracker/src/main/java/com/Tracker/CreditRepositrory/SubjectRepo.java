package com.Tracker.CreditRepositrory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Tracker.Entity.SubjectData;
@Repository
public interface SubjectRepo extends JpaRepository<SubjectData, Integer>{
	SubjectData findByCourseCode(String courseCode);


}
