package com.Tracker.Entity;

import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreditData {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int sno;
	 
	private String regdNo;
	
	private String name ;
	
	private String sem;
	
	private String subjectCode;
	private String subjectName;
	
	private String type;
	
	private String credits;	
	
	private String grade;
	
	
	
	

}
