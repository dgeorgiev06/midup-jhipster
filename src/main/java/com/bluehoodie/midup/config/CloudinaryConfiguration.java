package com.bluehoodie.midup.config;

import com.cloudinary.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import com.cloudinary.*;

@Configuration
public class CloudinaryConfiguration {

    private final Logger log = LoggerFactory.getLogger(CloudinaryConfiguration.class);

    @Bean
    public Cloudinary cloundinaryInstance() {
        return new Cloudinary(ObjectUtils.asMap(
            "cloud_name", "geordi",
            "api_key", "452451871763885",
            "api_secret", "PoCuzdOxpzHbGCVkd5JLK1lUt2M"));
    }
}
