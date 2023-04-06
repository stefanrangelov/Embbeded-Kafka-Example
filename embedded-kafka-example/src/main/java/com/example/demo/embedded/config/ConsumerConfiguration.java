package com.example.demo.embedded.config;

import com.example.demo.embedded.model.Company;
import com.example.demo.embedded.model.Department;
import com.example.demo.embedded.model.Employee;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class ConsumerConfiguration {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        return props;
    }

    @Bean
    public ConsumerFactory<Integer, Employee> employeeConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs(), new IntegerDeserializer(),
                new JsonDeserializer<>(Employee.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Integer, Employee> employeeKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Integer, Employee> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(employeeConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<Integer, Department> departmentConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs(), new IntegerDeserializer(),
                new JsonDeserializer<>(Department.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Integer, Department> departmentKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Integer, Department> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(departmentConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<Integer, Company> companyConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs(), new IntegerDeserializer(),
                new JsonDeserializer<>(Company.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Integer, Company> companyKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Integer, Company> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(companyConsumerFactory());
        return factory;
    }

}