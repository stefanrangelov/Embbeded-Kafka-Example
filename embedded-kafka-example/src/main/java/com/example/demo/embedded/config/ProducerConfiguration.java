package com.example.demo.embedded.config;

import com.example.demo.embedded.model.Company;
import com.example.demo.embedded.model.Department;
import com.example.demo.embedded.model.Employee;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
class ProducerConfiguration {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ProducerFactory<Integer, Employee> employeeProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public ProducerFactory<Integer, Department> departmentProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public ProducerFactory<Integer, Company> companyProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, EmployeeSerializer.class);
        return props;
    }

    @Bean
    public KafkaTemplate<Integer, Employee> employeeProducer() {
        return new KafkaTemplate<>(employeeProducerFactory());
    }

    @Bean
    public KafkaTemplate<Integer, Department> departmentProducer() {
        return new KafkaTemplate<>(departmentProducerFactory(), Collections.singletonMap(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, DepartmentSerializer.class));
    }

    @Bean
    public KafkaTemplate<Integer, Company> companyProducer() {
        return new KafkaTemplate<>(companyProducerFactory(), Collections.singletonMap(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, CompanySerializer.class));
    }
}