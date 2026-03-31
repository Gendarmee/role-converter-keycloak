package com.gendarme.role.converter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "converter.keycloak")
public class ConverterProperties {
    private String clientId;
    private boolean realmRolesEnabled = true;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public boolean isRealmRolesEnabled() {
        return realmRolesEnabled;
    }

    public void setRealmRolesEnabled(boolean realmRolesEnabled) {
        this.realmRolesEnabled = realmRolesEnabled;
    }
}