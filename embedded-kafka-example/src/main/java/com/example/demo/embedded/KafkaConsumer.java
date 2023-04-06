package com.example.demo.embedded;

import com.example.demo.embedded.model.Company;
import com.example.demo.embedded.model.Department;
import com.example.demo.embedded.model.Employee;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

@Component
public class KafkaConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);

    private Set<Employee> employees = new HashSet<>();
    private Set<Department> departments = new HashSet<>();
    private Company company;

    @KafkaListener(topics = "employees", groupId = "employee_consumer", containerFactory = "employeeKafkaListenerContainerFactory")
    public void listenEmployees(ConsumerRecord<Integer, Employee> consumerRecord) {
        LOGGER.info("received payload='{}'", consumerRecord.toString());

        employees.add(consumerRecord.value());
    }

    @KafkaListener(topics = "departments", groupId = "department_consumer", containerFactory = "departmentKafkaListenerContainerFactory")
    public void listenDepartments(ConsumerRecord<Integer, Department> consumerRecord) {
        LOGGER.info("received payload='{}'", consumerRecord.toString());

        departments.add(consumerRecord.value());
    }

    @KafkaListener(topics = "company", groupId = "company_consumer", containerFactory = "companyKafkaListenerContainerFactory")
    public void listenCompanies(ConsumerRecord<Integer, Company> consumerRecord) {
        LOGGER.info("received payload='{}'", consumerRecord.toString());

        company = consumerRecord.value();
    }

    public void clear() {
        this.employees = new HashSet<>();
        this.departments = new HashSet<>();
        this.company = null;
    }

    public Set<Employee> getEmployees() {
        return employees;
    }

    public Set<Department> getDepartments() {
        return departments;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}
