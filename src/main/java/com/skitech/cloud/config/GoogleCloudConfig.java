/*package com.skitech.cloud.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class GoogleCloudConfig {

	//@Autowired
    //private ResourceLoader resourceLoader;
	
    @Bean
    public Storage storage() throws IOException {
    	
    	
    	 //Getting the resource using the ResourceLoader
        // Resource resource = resourceLoader.getResource("classpath:" + "service-account.json\"");
        return StorageOptions.newBuilder()
                .setCredentials(GoogleCredentials.fromStream(new FileInputStream("D:/skitech-454008-8e0443b09950.json")))
                .build()
                .getService();
    }
}*/

