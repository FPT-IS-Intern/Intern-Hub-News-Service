package com.intern.hub.news.app;

import com.intern.hub.library.common.annotation.EnableGlobalExceptionHandler;
import com.intern.hub.starter.security.annotation.EnableSecurity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(
    exclude = {UserDetailsServiceAutoConfiguration.class},
    scanBasePackages = {
        "com.intern.hub.news.app",
        "com.intern.hub.news.api",
        "com.intern.hub.news.infra",
        "com.intern.hub.news.core",
    }
)
@EnableSecurity
@EnableGlobalExceptionHandler
@EnableJpaRepositories(basePackages = "com.intern.hub.news.infra.persistence.repository.jpa")
@EntityScan(basePackages = "com.intern.hub.news.infra.persistence.entity")
public class NewsServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(NewsServiceApplication.class, args);
  }

}
