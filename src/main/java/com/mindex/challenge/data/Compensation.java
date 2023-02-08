package com.mindex.challenge.data;

import java.time.LocalDateTime;

public class Compensation {
    private String employeeId;
    private double salary;
    private LocalDateTime effectiveDate;

    public Compensation() {
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public void setEffectiveDate(LocalDateTime effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public double getSalary() {
        return salary;
    }

    public LocalDateTime getEffectiveDate() {
        return effectiveDate;
    }
}
