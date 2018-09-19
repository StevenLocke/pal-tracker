package io.pivotal.pal.tracker;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mysql.cj.jdbc.MysqlDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@SpringBootApplication
public class PalTrackerApplication {
  
  @Value("${SPRING_DATASOURCE_URL}")
  private String mySqlUrl;

  public static void main(String[] args) {
    SpringApplication.run(PalTrackerApplication.class, args);
  }

  @Bean
  TimeEntryRepository timeEntryRepository() {
    MysqlDataSource dataSource = new MysqlDataSource();
    dataSource.setURL(mySqlUrl);
    return new JdbcTimeEntryRepository(dataSource);
  }

  @Bean
  ObjectMapper objectMapper() {
    return new Jackson2ObjectMapperBuilder().json()
        .serializationInclusion(JsonInclude.Include.NON_NULL) // Don’t include null values
        .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS) //ISODate
        .modules(new JavaTimeModule())
        .build();
  }
}