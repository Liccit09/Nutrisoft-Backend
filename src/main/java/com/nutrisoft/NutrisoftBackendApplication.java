package com.nutrisoft;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@EnableAsync
@SpringBootApplication
public class NutrisoftBackendApplication {

  public static void main(String[] args) {
    SpringApplication.run(NutrisoftBackendApplication.class, args);
  }
}
