package com.summer.school.retrofitcourse.model;

import com.google.gson.annotations.SerializedName;

/**
 * POJO for Employee Data
 */
public class EmployeeData {

    private int id;

    @SerializedName("employee_name")
    private String employeeName;

    @SerializedName("employee_salary")
    private int employeeSalary;

    @SerializedName("employee_age")
    private int emplyeeAge;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public int getEmployeeSalary() {
        return employeeSalary;
    }

    public void setEmployeeSalary(int employeeSalary) {
        this.employeeSalary = employeeSalary;
    }

    public int getEmplyeeAge() {
        return emplyeeAge;
    }

    public void setEmplyeeAge(int emplyeeAge) {
        this.emplyeeAge = emplyeeAge;
    }
}
