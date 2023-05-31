package com.onix.worldtour.controller.data;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "firebase")
public class FileProperties {
    private String bucketName;

    private String imageUrl;

    private String bucketFolderName;
}
