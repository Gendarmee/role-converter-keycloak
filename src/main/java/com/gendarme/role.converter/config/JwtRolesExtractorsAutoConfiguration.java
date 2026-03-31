package com.gendarme.role.converter.config;

import com.gendarme.role.converter.extractor.impl.KeycloakClientRolesExtractor;
import com.gendarme.role.converter.extractor.impl.KeycloakRealmRolesExtractor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtRolesExtractorsAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(
            prefix = "converter.keycloak",
            name = "realm-roles-enabled",
            havingValue = "true",
            matchIfMissing = true
    )
    public KeycloakRealmRolesExtractor keycloakRealmRolesExtractor() {
        return new KeycloakRealmRolesExtractor();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "converter.keycloak", name = "client-id")
    public KeycloakClientRolesExtractor keycloakClientRolesExtractor(
            ConverterProperties properties
    ) {
        return new KeycloakClientRolesExtractor(properties.getClientId());
    }
}