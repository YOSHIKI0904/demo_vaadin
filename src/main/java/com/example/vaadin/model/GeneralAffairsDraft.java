package com.example.vaadin.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 総務依頼フォームの入力内容をセッションに保持するための DTO。
 */
public class GeneralAffairsDraft implements Serializable {

    private static final long serialVersionUID = 1L;

    private String applicantId;
    private String applicantName;
    private String department;
    private String requestType;
    private String description;
    private LocalDateTime updatedAt;

    public String getApplicantId() {
        return applicantId;
    }

    public void setApplicantId(String applicantId) {
        this.applicantId = applicantId;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
