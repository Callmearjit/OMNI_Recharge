package com.recharge_service.recharge_service.dto;

public class PaymentResponse
{
	private Long transactionId;
    private String status;
    private String message;

    public Long getTransactionId() {
    	return transactionId;
    }
    public void setTransactionId(Long transactionId) { 
    	this.transactionId = transactionId;
    }

    public String getStatus() {
    	return status; 
    }
    public void setStatus(String status) {
    	this.status = status;
    }

    public String getMessage() {
    	return message; 
    }
    public void setMessage(String message) {
    	this.message = message;
    }

}
