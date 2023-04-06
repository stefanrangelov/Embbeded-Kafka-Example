package com.example.demo.embedded;

import com.example.demo.embedded.model.Company;
import com.example.demo.embedded.model.Department;
import com.example.demo.embedded.model.Employee;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.awaitility.Awaitility;
import org.json.JSONException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.event.annotation.BeforeTestClass;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.hamcrest.Matchers.equalTo;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 2, count = 2, topics = {"employees", "departments", "company"}, brokerProperties = {
        "auto.create.topics.enable=false"
})
class EmbeddedKafkaIntegrationTest {

    @Autowired
    public EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private KafkaConsumer consumer;

    @Autowired
    private KafkaProducer producer;

    @Autowired
    private CompanyService companyService;

    private ObjectMapper objectMapper = new ObjectMapper();
    private TestData testData;


    @AfterEach
    void setup() throws ExecutionException, InterruptedException, TimeoutException {
        consumer.clear();
    }

    @Test
    public void testEmployeeTopic() {
        List<Employee> employees = getTestData().getEmployees();
        for (Employee employee : employees) {
            producer.sendEmployee(employee);
        }
        Awaitility.await().atMost(3, TimeUnit.SECONDS).pollDelay(100, TimeUnit.MILLISECONDS)
                .until(() -> consumer.getEmployees().size(), equalTo(employees.size()));
    }

    @Test
    public void testDepartmentsTopic() {
        List<Department> departments = getTestData().getDepartments();
        for (Department department : departments) {
            producer.sendDepartment(department);
        }
        Awaitility.await().atMost(3, TimeUnit.SECONDS).pollDelay(100, TimeUnit.MILLISECONDS)
                .until(() -> consumer.getDepartments().size(), equalTo(departments.size()));
    }

    @Test
    public void testCompanyTopic() throws Exception {
        Company company = getTestData().getCompany();
        producer.sendCompany(company);

        Awaitility.await().atMost(3, TimeUnit.SECONDS).pollDelay(100, TimeUnit.MILLISECONDS)
                .until(() -> consumer.getCompany() != null);
    }

    @Test
    public void test() throws IOException, ExecutionException, InterruptedException, JSONException {
        ObjectMapper mapper = new ObjectMapper();
        TestData testData = getTestData();

        List<Employee> employees = testData.getEmployees();
        List<Department> departments = testData.getDepartments();

        employees.forEach(e -> producer.sendEmployee(e));
        departments.forEach(d -> producer.sendDepartment(d));

        Awaitility.await().atMost(3, TimeUnit.SECONDS).until(() -> consumer.getEmployees() != null
                && consumer.getDepartments() != null);
        companyService.get();
        Awaitility.await().atMost(3, TimeUnit.SECONDS).until(() -> consumer.getCompany() != null);
        Company company = consumer.getCompany();

        String s1 = mapper.writeValueAsString(company);
        String s2 = mapper.writeValueAsString(testData.getCompany());

        JSONAssert.assertEquals(s1, s2, JSONCompareMode.LENIENT);
    }

    private TestData getTestData() {
        if (testData != null) {
            return testData;
        }
        try {
            return objectMapper.readValue(new ClassPathResource("data.json").getFile(), TestData.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
