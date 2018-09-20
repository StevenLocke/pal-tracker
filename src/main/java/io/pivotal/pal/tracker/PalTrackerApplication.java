package io.pivotal.pal.tracker;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mysql.cj.jdbc.MysqlDataSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.writer.DefaultCounterService;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@SpringBootApplication
public class PalTrackerApplication {

  public static void main(String[] args) {
    SpringApplication.run(PalTrackerApplication.class, args);
  }

  @Bean
  TimeEntryRepository timeEntryRepository() {
    MysqlDataSource dataSource = new MysqlDataSource();
    dataSource.setUrl(System.getenv("SPRING_DATASOURCE_URL"));
    return new JdbcTimeEntryRepository(dataSource);
  }

//  @Bean
//  CounterService counterService() {
//    return new DefaultCounterService("");
//  }

  @Bean
  ObjectMapper objectMapper() {
    return new Jackson2ObjectMapperBuilder().json()
        .serializationInclusion(JsonInclude.Include.NON_NULL) // Don’t include null values
        .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS) //ISODate
        .modules(new JavaTimeModule())
        .build();
  }
}