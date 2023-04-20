package com.example.demo;

import com.example.demo.embedded.model.Company;
import com.example.demo.embedded.model.Department;
import com.example.demo.embedded.model.Employee;

import java.util.List;

public class TestData {
    private List<Employee> employees;
    private List<Department> departments;
    private Company company;

    public TestData() {
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public List<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}
