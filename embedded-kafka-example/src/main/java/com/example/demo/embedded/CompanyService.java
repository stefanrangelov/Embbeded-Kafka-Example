package com.example.demo.embedded;

import com.example.demo.embedded.model.Company;
import com.example.demo.embedded.model.Department;
import com.example.demo.embedded.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class CompanyService {
    @Autowired
    private KafkaConsumer kafkaConsumer;

    @Autowired
    private KafkaTemplate<Integer, Company> companyProducer;


    // combine data from "employee" and "department" topic and produce to new topic
    public CompletableFuture<SendResult<Integer, Company>> get() {
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
        return companyProducer.send("company", company).completable();
    }
}
