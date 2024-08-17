 package com.Tracker.CreditRepositrory;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import com.Tracker.Entity.SubjectDataCIVIL;

public interface SubjectRepoCIVIL extends JpaRepository<SubjectDataCIVIL, Integer> {

	

}
