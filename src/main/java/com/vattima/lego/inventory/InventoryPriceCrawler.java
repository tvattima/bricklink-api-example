package com.vattima.lego.inventory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@Slf4j
@SpringBootApplication(scanBasePackages = {"net.bricklink", "com.bricklink", "com.vattima"})
@EnableConfigurationProperties
public class InventoryPriceCrawler {
    public static void main(String[] args) {
        SpringApplication.run(InventoryPriceCrawler.class, args);
    }
}
