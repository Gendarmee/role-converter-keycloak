package com.gendarme.role.converter.config;

import com.gendarme.role.converter.extractor.JwtClaimRolesExtractor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

public class KeycloakConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    private final List<JwtClaimRolesExtractor> extractors;

    public KeycloakConverter(List<JwtClaimRolesExtractor> extractors) {
        this.extractors = extractors;
    }

    @Override
        public Collection<GrantedAuthority> convert(Jwt source) {
            Set<String> roles = extractors.stream()
                    .flatMap(extractor -> extractor.extract(source).stream())
                    .collect(Collectors.toSet());

            return roles.stream()
                    .map(String::toUpperCase)
                    .map(role -> "ROLE_" + role)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }
}