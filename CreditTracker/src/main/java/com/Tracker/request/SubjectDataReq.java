package com.Tracker.request;

import lombok.Data;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.validation.constraints.Min;

@Data
public class SubjectDataReq {

    @NotNull(message = "Programme cannot be null")
    @NotEmpty(message = "Programme cannot be empty")
    private String programme;
	
    @NotNull(message = "Branch cannot be null")
    @NotEmpty(message = "Branch cannot be empty")
    private String branch;
	
    @NotNull(message = "Basket cannot be null")
    @Pattern(regexp = "Basket I|Basket II|Basket III|Basket IV|Basket V", 
             message = "Basket must be one of the following: Basket I, Basket II, Basket III, Basket IV, Basket V")
    private String basket;
	
    @NotNull(message = "Course type cannot be null")
    @NotEmpty(message = "Course type cannot be empty")
    private String courseType;
	
    @NotNull(message = "Course code cannot be null")
    @NotEmpty(message = "Course code cannot be empty")
    private String courseCode;
	
    @NotNull(message = "Subject name cannot be null")
    @NotEmpty(message = "Subject name cannot be empty")
    @Pattern(regexp = "^[a-zA-Z0-9 ]+$", 
             message = "Subject name cannot contain symbols or special characters")
    private String subjectName;
	
    @Min(value = 1, message = "Credit must be at least 1")
    private int credit;
	
    @NotNull(message = "Type cannot be null")
    @NotEmpty(message = "Type cannot be empty")
    private String type;

}
