package com.gendarme.role.converter.extractor.impl;

import com.gendarme.role.converter.extractor.AbstractJwtRolesExtractor;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.function.Supplier;

public class KeycloakRealmRolesExtractor extends AbstractJwtRolesExtractor {

    @Override
    public <T extends Collection<String>> T extract(Jwt source, Supplier<T> factory) {
        Object claimObj = source.getClaim("realm_access");
        return extractRolesFromMap(claimObj, factory);
    }
}
