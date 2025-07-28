package com.example.ium._core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.example.ium.chat.domain.mongo.repository")
@EnableJpaRepositories(basePackages = {
        "com.example.ium.chat.domain.jpa.repository",
        "com.example.ium.member.domain.repository",
        "com.example.ium.money.domain.repository",
        "com.example.ium.workrequest.repository",
        "com.example.ium.specialization.domain.repository",
        "com.example.ium.report.domain.repository",
})
//@EnableRedisRepositories(basePackages = {
//        "com.example.ium.member.infrastructure.repository"
//})
public class RepositoryConfig {
}
