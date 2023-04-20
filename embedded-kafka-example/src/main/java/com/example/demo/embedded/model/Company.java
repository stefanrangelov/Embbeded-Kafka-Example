package com.example.demo.embedded.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Company {
    private Map<String, Set<Employee>> employeesByDepartment;

    public Company() {
        this.employeesByDepartment = new HashMap<>();
    }

    public Map<String, Set<Employee>> getEmployeesByDepartment() {
        return employeesByDepartment;
    }

    public void setEmployeesByDepartment(Map<String, Set<Employee>> employeesByDepartment) {
        this.employeesByDepartment = employeesByDepartment;
    }

    @Override
    public String toString() {
        return "Company{" +
                "employeesByDepartment=" + employeesByDepartment +
                '}';
    }
}