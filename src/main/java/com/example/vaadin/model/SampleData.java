package com.example.vaadin.model;

import java.io.Serializable;

/**
 * サンプルデータモデル
 */
public class SampleData implements Serializable {
    private Long id;
    private String name;
    private String email;
    private String status;
    private Integer age;

    public SampleData() {
    }

    public SampleData(Long id, String name, String email, String status, Integer age) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.status = status;
        this.age = age;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "SampleData{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", status='" + status + '\'' +
                ", age=" + age +
                '}';
    }
}
