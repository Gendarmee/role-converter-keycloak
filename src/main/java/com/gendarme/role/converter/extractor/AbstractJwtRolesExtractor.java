package com.gendarme.role.converter.extractor;

import com.gendarme.role.converter.extractor.JwtClaimRolesExtractor;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

abstract public class AbstractJwtRolesExtractor implements JwtClaimRolesExtractor {
    protected <T extends Collection<String>> T extractRolesFromMap(Object obj, Supplier<T> factory) {
        if (!(obj instanceof Map<?, ?> map)) {
            return factory.get();
        }
        Object rolesObj = map.get("roles");
        if (!(rolesObj instanceof List<?> rawRoles)) {
            return factory.get();
        }

        T collection = factory.get();
        rawRoles.stream()
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .forEach(collection::add);
        return collection;
    }
}
