package com.recharge_service.recharge_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class RechargeRequest {

    @NotBlank
    @Pattern(regexp = "\\d{10}", message = "must be a 10-digit mobile number")
    private String mobileNumber;

    @NotNull
    private Long planId;

    @NotBlank
    private String idempotencyKey;

    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }

    public Long getPlanId() { return planId; }
    public void setPlanId(Long planId) { this.planId = planId; }

    public String getIdempotencyKey() { return idempotencyKey; }
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }
}
