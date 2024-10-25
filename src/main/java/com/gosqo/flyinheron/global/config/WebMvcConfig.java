package com.gosqo.flyinheron.global.config;

import com.gosqo.flyinheron.domain.DefaultImageManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    private final String filePath = DefaultImageManager.LOCAL_STORAGE_DIR;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/file-storage/**")
                .addResourceLocations("file:" + filePath + File.separator);
    }
}
