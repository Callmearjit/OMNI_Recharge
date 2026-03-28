package com.notification_service.notification_service.dto;

import java.time.LocalDateTime;

public class TransactionEvent {

	 private Long id;
	    private Long rechargeId;
	    private Long userId;
	    private Double amount;
	    private String status;
	    private String transactionRef;
	    private LocalDateTime createdAt;

	    public Long getId() {
	    	return id;
	    	}
	    public void setId(Long id) { 
	    	this.id = id; 
	    }

	    public Long getRechargeId() {
	    	return rechargeId; 
	    }
	    public void setRechargeId(Long rechargeId) {
	    	this.rechargeId = rechargeId; 
	    	}

	    public Long getUserId() { 
	    	return userId;
	    	}
	    public void setUserId(Long userId) {
	    	this.userId = userId; 
	    }

	    public Double getAmount() { 
	    	return amount;
	    }
	    public void setAmount(Double amount) {
	    	this.amount = amount; 
	    	}

	    public String getStatus() {
	    	return status; 
	    	}
	    public void setStatus(String status) {
	    	this.status = status; 
	    	}

	    public String getTransactionRef() {
	    	return transactionRef;
	    	}
	    public void setTransactionRef(String transactionRef) { 
	    	this.transactionRef = transactionRef;
	    	}

	    public LocalDateTime getCreatedAt() {
	    	return createdAt; 
	    	}
	    public void setCreatedAt(LocalDateTime createdAt) {
	    	this.createdAt = createdAt;
	    	}
	}
