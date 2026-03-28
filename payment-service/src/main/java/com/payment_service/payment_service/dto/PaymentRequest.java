
package com.payment_service.payment_service.dto;

public class PaymentRequest {
	private Long rechargeId;
    private Double amount;
    private String idempotencyKey;
	public String getIdempotencyKey() {
		return idempotencyKey;
	}
	public void setIdempotencyKey(String idempotencyKey) {
		this.idempotencyKey = idempotencyKey;
	}
	public Long getRechargeId() {
		return rechargeId;
	}
	public void setRechargeId(Long rechargeId) {
		this.rechargeId = rechargeId;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
    
    

}