package com.example.demo.embedded;

import com.example.demo.embedded.model.Company;
import com.example.demo.embedded.model.Department;
import com.example.demo.embedded.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class CompanyService {
    @Autowired
    private KafkaConsumer kafkaConsumer;

    @Autowired
    private KafkaTemplate<Integer, Company> companyProducer;

    public void get() throws ExecutionException, InterruptedException {
        Set<Employee> employees = kafkaConsumer.getEmployees();
        Map<Integer, Department> departments = kafkaConsumer.getDepartments().stream().collect(Collectors.toMap(Department::getId, d -> d));

        Map<String, Set<Employee>> companyMap = new HashMap<>();
        employees.forEach(employee -> {
            int departmentId = employee.getDepartmentId();
            Department department = departments.get(departmentId);
            companyMap.compute(department.getName(), (k, v) -> {
                if (v == null) {
                    v = new HashSet<>();
                }
                v.add(employee);
                return v;
            });
        });
        System.out.println(companyMap);
        Company company = new Company();
        company.setEmployeesByDepartment(companyMap);
        companyProducer.send("company",company).get();
    }


}
