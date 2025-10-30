package com.example.vaadin.model;

import java.io.Serializable;

/**
 * 社員の基本プロフィール情報を保持するモデル。
 */
public class EmployeeProfile implements Serializable {

    private Long employeeNumber;
    private String fullName;
    private String email;
    private String employmentStatus;
    private Integer yearsOfService;

    public EmployeeProfile() {
    }

    public EmployeeProfile(Long employeeNumber, String fullName, String email, String employmentStatus, Integer yearsOfService) {
        this.employeeNumber = employeeNumber;
        this.fullName = fullName;
        this.email = email;
        this.employmentStatus = employmentStatus;
        this.yearsOfService = yearsOfService;
    }

    public Long getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(Long employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmploymentStatus() {
        return employmentStatus;
    }

    public void setEmploymentStatus(String employmentStatus) {
        this.employmentStatus = employmentStatus;
    }

    public Integer getYearsOfService() {
        return yearsOfService;
    }

    public void setYearsOfService(Integer yearsOfService) {
        this.yearsOfService = yearsOfService;
    }

    @Override
    public String toString() {
        return "EmployeeProfile{" +
            "employeeNumber=" + employeeNumber +
            ", fullName='" + fullName + '\'' +
            ", email='" + email + '\'' +
            ", employmentStatus='" + employmentStatus + '\'' +
            ", yearsOfService=" + yearsOfService +
            '}';
    }
}
