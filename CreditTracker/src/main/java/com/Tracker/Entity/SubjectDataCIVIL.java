package com.Tracker.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubjectDataCIVIL{
	

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int sno;
	
	private String programme;
	
	private String branch;
	
	private String basket;
	
	private String courseType;
	
	private String courseCode;
	
	private String SubjectName;
	
	private int credit;
	
	private String type;

}
