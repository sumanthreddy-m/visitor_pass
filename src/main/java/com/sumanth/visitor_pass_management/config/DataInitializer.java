package com.sumanth.visitor_pass_management.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sumanth.visitor_pass_management.entity.VisitorType;
import com.sumanth.visitor_pass_management.repository.VisitorTypeRepository;

import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(VisitorTypeRepository visitorTypeRepository) {
        return args -> {
            List<VisitorType> existingTypes = visitorTypeRepository.findAll();

            if (existingTypes.isEmpty()) {
                List<VisitorType> defaultVisitorTypes = List.of(
                        new VisitorType(1, "Parents"),
                        new VisitorType(2, "Child"),
                        new VisitorType(3, "Sibling"),
                        new VisitorType(4, "Spouse"),
                        new VisitorType(5, "Client")
                );
                visitorTypeRepository.saveAll(defaultVisitorTypes);
//                System.out.println("Visitor Types Initialized");
            } else {
                System.out.println("Visitor Types already exist in database");
            }
        };
    }
}