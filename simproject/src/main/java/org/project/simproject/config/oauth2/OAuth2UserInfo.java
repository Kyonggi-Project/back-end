package org.project.simproject.config.oauth2;

public interface OAuth2UserInfo {
    String getProviderId();
    String getName();
    String getEmail();
    String getProvider();
}
