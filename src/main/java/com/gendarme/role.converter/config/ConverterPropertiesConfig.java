package com.gendarme.role.converter.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ConverterProperties.class)
public class ConverterPropertiesConfig {
}