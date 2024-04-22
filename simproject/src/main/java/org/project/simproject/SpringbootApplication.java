package org.project.simproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories(basePackages = "org.project.simproject.repository.mongoRepo")
@EnableJpaRepositories(basePackages = "org.project.simproject.repository.entityRepo")
@EnableJpaAuditing
@SpringBootApplication
public class SpringbootApplication {
    public static void main(String[] args){
        SpringApplication.run(SpringbootApplication.class, args);
    }
}
