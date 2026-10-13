package com.gendarme.roleconverter;

import com.gendarme.role.converter.config.KeycloakConverter;
import com.gendarme.role.converter.extractor.JwtClaimRolesExtractor;
import com.gendarme.role.converter.extractor.impl.KeycloakClientRolesExtractor;
import com.gendarme.role.converter.extractor.impl.KeycloakRealmRolesExtractor;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class RoleConverterApplicationTests {

    @Test
    void convertsRealmAndConfiguredClientRolesToSpringAuthorities() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("realm_access", Map.of("roles", List.of("admin", "reader")))
                .claim("resource_access", Map.of(
                        "bank-api", Map.of("roles", List.of("reader", "client-manager")),
                        "account", Map.of("roles", List.of("ignored"))
                ))
                .build();

        List<JwtClaimRolesExtractor> extractors = List.of(
                new KeycloakRealmRolesExtractor(),
                new KeycloakClientRolesExtractor("bank-api")
        );

        KeycloakConverter converter = new KeycloakConverter(extractors);

        assertThat(converter.convert(jwt))
                .extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_READER", "ROLE_CLIENT-MANAGER");
    }

    @Test
    void returnsNoAuthoritiesWhenNoExtractorsAreConfigured() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("realm_access", Map.of("roles", List.of("admin")))
                .build();

        assertThat(new KeycloakConverter(List.of()).convert(jwt)).isEmpty();
    }

    @Test
    void ignoresMalformedRoleClaims() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("realm_access", Map.of("roles", List.of("admin", 42, true)))
                .build();

        KeycloakConverter converter = new KeycloakConverter(List.of(new KeycloakRealmRolesExtractor()));

        assertThat(converter.convert(jwt))
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_ADMIN");
    }
}
