
package com.recharge_service.recharge_service.dto;
//package com.payment_service.payment_service.dto;

//─────────────────────────────────────────────────────────────────────────────
//FIX: Added userId field (String) so recharge-service can pass the
//X-User-Id header value through the Feign call to payment-service.
//
//Previously PaymentService hardcoded txn.setUserId(1L) because there was
//no way to receive the actual user from the caller. Now recharge-service
//sets request.setUserId(userId) before calling paymentClient.processPayment().
//─────────────────────────────────────────────────────────────────────────────
public class PaymentRequest {

 private Long rechargeId;
 private Double amount;
 private String idempotencyKey;

 // userId as String — matches the JWT subject (username).
 // PaymentService will attempt Long.parseLong() and fall back to null
 // if the value is a non-numeric username string.
 private String userId;

 public Long getRechargeId() { return rechargeId; }
 public void setRechargeId(Long rechargeId) { this.rechargeId = rechargeId; }

 public Double getAmount() { return amount; }
 public void setAmount(Double amount) { this.amount = amount; }

 public String getIdempotencyKey() { return idempotencyKey; }
 public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }

 public String getUserId() { return userId; }
 public void setUserId(String userId) { this.userId = userId; }
}