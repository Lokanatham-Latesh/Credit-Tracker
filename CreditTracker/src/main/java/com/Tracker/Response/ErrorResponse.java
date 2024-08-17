package com.Tracker.Response;

import java.util.List;

public class ErrorResponse {  
    private List<String> data;  
    private String errorMessage;  

    public ErrorResponse(List<String> data, String errorMessage) {  
        this.data = data;  
        this.errorMessage = errorMessage;  
    }

	public List<String> getData() {
		return data;
	}

	public void setData(List<String> data) {
		this.data = data;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}  

    // Getters and setters  
}
