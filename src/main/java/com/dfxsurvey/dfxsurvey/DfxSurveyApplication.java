package com.dfxsurvey.dfxsurvey;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.dfxsurvey.dfxsurvey")

@EntityScan("com.dfxsurvey.dfxsurvey.dao.entity")
@EnableJpaRepositories("com.dfxsurvey.dfxsurvey.dao.repository")
public class DfxSurveyApplication {

    public static void main(String[] args) {
        SpringApplication.run(DfxSurveyApplication.class, args);
    }

}
