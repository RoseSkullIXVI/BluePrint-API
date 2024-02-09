package com.blueprint.api;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.blueprint.api.Service_Implementation.FileStorageInterface;

import jakarta.annotation.Resource;



@SpringBootApplication
public class ApiApplication implements CommandLineRunner { 
    @Resource
    FileStorageInterface storageService;
    public static void main(String[] args) {
      SpringApplication.run(ApiApplication.class, args);
    }
    @Override
    public void run(String... args) throws Exception {
      storageService.init();
    }
}