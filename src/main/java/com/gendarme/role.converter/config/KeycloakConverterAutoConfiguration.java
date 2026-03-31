package com.gendarme.role.converter.config;

import com.gendarme.role.converter.extractor.JwtClaimRolesExtractor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;

@Configuration
public class KeycloakConverterAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public KeycloakConverter keycloakConverter(
            ObjectProvider<List<JwtClaimRolesExtractor>> extractorsProvider
    ) {
        List<JwtClaimRolesExtractor> extractors =
                extractorsProvider.getIfAvailable(Collections::emptyList);

        return new KeycloakConverter(extractors);
    }
}