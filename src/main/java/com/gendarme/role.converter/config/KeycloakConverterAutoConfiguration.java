package com.gendarme.role.converter.config;

import com.gendarme.role.converter.extractor.JwtClaimRolesExtractor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import java.util.List;

@AutoConfiguration
public class KeycloakConverterAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(KeycloakConverter.class)
    public KeycloakConverter keycloakConverter(
            ObjectProvider<JwtClaimRolesExtractor> extractorsProvider
    ) {
        List<JwtClaimRolesExtractor> extractors =
                extractorsProvider.orderedStream().toList();

        return new KeycloakConverter(extractors);
    }
}
