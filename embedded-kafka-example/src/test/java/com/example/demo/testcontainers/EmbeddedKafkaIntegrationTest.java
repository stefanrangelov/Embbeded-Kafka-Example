package com.example.demo.testcontainers;

import com.example.demo.embedded.CompanyService;
import com.example.demo.embedded.KafkaConsumer;
import com.example.demo.embedded.KafkaProducer;
import com.example.demo.embedded.model.Company;
import com.example.demo.embedded.model.Department;
import com.example.demo.embedded.model.Employee;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.awaitility.Awaitility;
import org.json.JSONException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.example.demo.Utils.getTestData;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest
@DirtiesContext
@Testcontainers
class EmbeddedKafkaIntegrationTest {
    @Container
    static KafkaContainer kafkaContainer =
            new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    @Autowired
    private KafkaConsumer consumer;

    @Autowired
    private KafkaProducer producer;

    @Autowired
    private CompanyService companyService;


    private final ObjectMapper objectMapper = new ObjectMapper();

    @AfterEach
    void setup() {
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
    public void testCompanyTopic() {
        Company company = getTestData().getCompany();
        producer.sendCompany(company);

        Awaitility.await().atMost(3, TimeUnit.SECONDS).pollDelay(100, TimeUnit.MILLISECONDS)
                .until(() -> consumer.getCompany() != null);
    }

    @Test
    public void companyServiceTest() throws InterruptedException {
        List<Employee> employees = getTestData().getEmployees();
        List<Department> departments = getTestData().getDepartments();

        employees.forEach(e -> producer.sendEmployee(e));
        departments.forEach(d -> producer.sendDepartment(d));

        Awaitility.await().atMost(3, TimeUnit.SECONDS).until(() -> consumer.getEmployees() != null
                && consumer.getDepartments() != null);

        companyService.get().thenAccept(result -> {
            Company company = result.getProducerRecord().value();
            try {
                String s1 = objectMapper.writeValueAsString(company);
                String s2 = objectMapper.writeValueAsString(getTestData().getCompany());
                JSONAssert.assertEquals(s1, s2, JSONCompareMode.LENIENT);
            } catch (JsonProcessingException | JSONException e) {
                throw new RuntimeException(e);
            }
        }).orTimeout(3, TimeUnit.SECONDS);
    }
}
