package com.example.demo.embedded;

import com.example.demo.embedded.model.Company;
import com.example.demo.embedded.model.Department;
import com.example.demo.embedded.model.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
public class KafkaProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducer.class);

    @Autowired
    private KafkaTemplate<Integer, Employee> employeeProducer;

    @Autowired
    private KafkaTemplate<Integer, Department> departmentProducer;

    @Autowired
    private KafkaTemplate<Integer, Company> companyProducer;

    public void sendEmployee( Employee payload)  {
        LOGGER.info("sending payload='{}' to topic='{}'", payload, "employees");
        try {
            employeeProducer.send("employees", payload).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendDepartment(Department payload)  {
        LOGGER.info("sending payload='{}' to topic='{}'", payload, "departments");
        departmentProducer.send("departments", payload);
    }

    public void sendCompany(Company payload)  {
        LOGGER.info("sending payload='{}' to topic='{}'", payload, "company");
        try {
            companyProducer.send("company", payload).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
