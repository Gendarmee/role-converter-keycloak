package com.gendarme.role.converter.extractor.impl;

import com.gendarme.role.converter.extractor.AbstractJwtRolesExtractor;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

public class KeycloakClientRolesExtractor extends AbstractJwtRolesExtractor {

    private final String clientName;

    public KeycloakClientRolesExtractor(String clientName) {
        this.clientName = clientName;
    }

    @Override
    public <T extends Collection<String>> T extract(Jwt source, Supplier<T> factory) {
        Object claimObj = source.getClaim("resource_access");
        if (!(claimObj instanceof Map<?, ?> resourceAccess)) {
            return factory.get();
        }

        Object clientObj = resourceAccess.get(clientName);
        return extractRolesFromMap(clientObj, factory);
    }
}
