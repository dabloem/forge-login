package org.jboss.forge.addon.login.commands;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.auth.oauth2.TokenRequest;
import com.google.api.client.auth.oauth2.Credential.AccessMethod;
import com.google.api.client.auth.openidconnect.IdTokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpExecuteInterceptor;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;

/**
 * IdTokenCodeFlow
 */
public class IdTokenCodeFlow extends AuthorizationCodeFlow {


    protected IdTokenCodeFlow(Builder builder) {
        super(builder);
    }

    @Override
    public AuthorizationCodeTokenRequest newTokenRequest(String authorizationCode) {
        // AuthorizationCodeTokenRequest req = 
        //     new AuthorizationCodeTokenRequest(getTransport(), getJsonFactory(), new GenericUrl(getTokenServerEncodedUrl()), authorizationCode) {


        // };
        // req.setResponseClass(IdTokenResponse.class);
        // return req;
        return new AuthorizationCodeTokenRequest(getTransport(), getJsonFactory(),
        new GenericUrl(getTokenServerEncodedUrl()), authorizationCode)
            .setClientAuthentication(getClientAuthentication())
            .setRequestInitializer(getRequestInitializer())
            .setResponseClass(IdTokenResponse.class)
            .setScopes(getScopes());
    }

    
}