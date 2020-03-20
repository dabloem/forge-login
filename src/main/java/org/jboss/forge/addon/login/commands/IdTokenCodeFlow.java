package org.jboss.forge.addon.login.commands;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.auth.openidconnect.IdTokenResponse;

/**
 * IdTokenCodeFlow
 */
public class IdTokenCodeFlow extends AuthorizationCodeFlow {


    protected IdTokenCodeFlow(Builder builder) {
        super(builder);
    }

    @Override
    public AuthorizationCodeTokenRequest newTokenRequest(String authorizationCode) {
        AuthorizationCodeTokenRequest req  = super.newTokenRequest(authorizationCode);
        req.setResponseClass(IdTokenResponse.class);
        return req;
    }

    
}