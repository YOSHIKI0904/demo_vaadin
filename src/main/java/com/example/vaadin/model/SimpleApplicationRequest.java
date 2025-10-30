package com.example.vaadin.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * シンプルな申請フォームで扱う入力値をまとめた DTO。
 * <p>
 * Vaadin Binder から Bean Validation を利用できるよう Jakarta Validation のアノテーションを付与している。
 * 必須項目や最大文字数に関するビジネスルールが変わった場合はこのクラスを更新する。
 */
public class SimpleApplicationRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "申請者IDを入力してください")
    @Size(max = 32, message = "申請者IDは32文字以内で入力してください")
    private String applicantId;

    @NotBlank(message = "申請者名を入力してください")
    @Size(max = 64, message = "申請者名は64文字以内で入力してください")
    private String applicantName;

    @NotBlank(message = "メールアドレスを入力してください")
    @Email(message = "メールアドレスの形式が不正です")
    @Size(max = 128, message = "メールアドレスは128文字以内で入力してください")
    private String contactEmail;

    @Size(max = 64, message = "所属部署は64文字以内で入力してください")
    private String department;

    @NotBlank(message = "申請区分を選択してください")
    private String requestType;

    @NotNull(message = "希望日を選択してください")
    private LocalDate desiredDate;

    @Size(max = 500, message = "申請内容の詳細は500文字以内で入力してください")
    private String description;

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

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
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

    public LocalDate getDesiredDate() {
        return desiredDate;
    }

    public void setDesiredDate(LocalDate desiredDate) {
        this.desiredDate = desiredDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
