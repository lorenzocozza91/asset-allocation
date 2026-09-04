package com.lorenzocozza.assetallocation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AssetAllocationApplication {
    public static void main(String[] args) {
        SpringApplication.run(AssetAllocationApplication.class, args);
    }
}
