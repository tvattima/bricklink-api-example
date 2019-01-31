package com.vattima.lego.sheet.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "lego.sheet")
@Getter
@Setter
public class LegoItemSheetProperties {
    private String dataStoreDir;
    private String clientSecretDir;
    private String id;
    private String workbook;
    private String range;
}
