package com.actionhouse.actionhouse.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

import java.nio.file.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        try {
            Path uploadPath = Paths.get(System.getProperty("user.dir"), "uploads")
                    .toAbsolutePath();
            Files.createDirectories(uploadPath);
            registry.addResourceHandler("/img/uploads/**")
                    .addResourceLocations("file:" + uploadPath + "\\");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}