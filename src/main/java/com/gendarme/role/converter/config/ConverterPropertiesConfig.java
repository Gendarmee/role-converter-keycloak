package com.gendarme.role.converter.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@AutoConfiguration
@EnableConfigurationProperties(ConverterProperties.class)
public class ConverterPropertiesConfig {
}
