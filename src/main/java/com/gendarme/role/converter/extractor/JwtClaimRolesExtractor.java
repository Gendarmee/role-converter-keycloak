package com.gendarme.role.converter.extractor;

import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public interface JwtClaimRolesExtractor {
    <T extends Collection<String>> T extract(Jwt source, Supplier<T> factory);

    default List<String> extract(Jwt source) {
        return extract(source, ArrayList::new);
    }
}