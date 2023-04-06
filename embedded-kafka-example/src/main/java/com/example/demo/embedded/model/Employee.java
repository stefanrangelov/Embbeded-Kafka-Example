package com.example.demo.embedded.model;

import java.util.UUID;

public class Employee {
    private String name;
    private UUID id;
    private int departmentId;

    public Employee(){}

    public Employee(String name, UUID id, int departmentId) {
        this.name = name;
        this.id = id;
        this.departmentId = departmentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", departmentId=" + departmentId +
                '}';
    }
}
