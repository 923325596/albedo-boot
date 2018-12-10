package com.albedo.java.common.config;

import com.albedo.java.common.data.util.JSR310PersistenceConverters;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.QueryLookupStrategy.Key;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EntityScan(basePackages = {"com.albedo.java.modules.*.domain"}, basePackageClasses = {JSR310PersistenceConverters.class})
@EnableJpaRepositories(value = {"com.albedo.java.modules.*.repository"}, queryLookupStrategy = Key.CREATE_IF_NOT_FOUND)
@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware")
@EnableTransactionManagement
public class DatabaseAutoConfiguration {

}
