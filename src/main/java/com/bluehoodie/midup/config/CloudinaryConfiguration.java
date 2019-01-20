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
            "cloud_name", "",
            "api_key", "",
            "api_secret", ""));
    }
}
