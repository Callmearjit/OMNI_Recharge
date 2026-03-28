package com.recharge_service.recharge_service.dto;

public class PlanResponse {

	private Long id;
    private Double amount;
    private String validity;

    public Long getId() {
    	return id;
    }
    public void setId(Long id) {
    	this.id = id; 
    }

    public Double getAmount() {
    	return amount;
    }
    public void setAmount(Double amount) {
    	this.amount = amount;
    }

    public String getValidity() {
    	return validity;
    }
    public void setValidity(String validity) {
    	this.validity = validity;
    }
}
