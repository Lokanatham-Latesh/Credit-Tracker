package com.Tracker.CreditRepositrory;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Tracker.Entity.SubjectDataECE;

public interface SubjectRepoECE extends JpaRepository<SubjectDataECE, Integer> {
}