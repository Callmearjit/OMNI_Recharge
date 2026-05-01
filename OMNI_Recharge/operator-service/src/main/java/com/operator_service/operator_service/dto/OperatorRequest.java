package com.operator_service.operator_service.dto;

public class OperatorRequest {

    private String name;

    public OperatorRequest() {}

    public OperatorRequest(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
