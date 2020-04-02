package org.forge.login.commands;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.StreamSupport;

import javax.inject.Inject;
import javax.naming.AuthenticationException;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;

import org.eclipse.microprofile.config.Config;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;

/**
 * OAuthAuthenticator
 */
public class OAuthAuthenticator implements Authenticator {

	/** Global instance of the HTTP transport. */
	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

	/** Global instance of the JSON factory. */
	static final JsonFactory JSON_FACTORY = new JacksonFactory();

	/**
	 * Global instance of the {@link DataStoreFactory}. The best practice is to make it a single
	 * globally shared instance across your application.
	 */
	private static MemoryDataStoreFactory DATA_STORE_FACTORY = new MemoryDataStoreFactory();

	/** OAuth 2 scope. */
    // private static final String SCOPE = "openid";
    private static final String OAUTH = "security.oauth2";
    protected static final String TOKEN = OAUTH + ".token";
	private static final String TOKEN_SERVER_URL =  OAUTH +".auth-token-url"; 
    private static final String AUTHORIZATION_SERVER_URL = OAUTH + ".auth-server-url";

    private static final String SCOPE = OAUTH + ".scope";
    private static final String ROLE =  OAUTH + ".role";
    private static final String PORT =  OAUTH + ".domain.port";
    private static final String DOMAIN = OAUTH + ".domain.url";

    protected static final String API_KEY = OAUTH + ".client-id";

    @Inject
    private Config config;

    @Override
    public Result authenticate(Optional<String> iss) throws AuthenticationException {
        // Default values if not provided
        String scope = config.getOptionalValue(getKey(SCOPE, iss), String.class).orElse("openid");
        String domain = config.getOptionalValue(getKey(DOMAIN, iss), String.class).orElse("localhost");
        int port = config.getOptionalValue(getKey(PORT, iss), Integer.class).orElse(-1);

        // set up authorization code flow
        try {
            AuthorizationCodeFlow flow = new AuthorizationCodeFlow.Builder(BearerToken.authorizationHeaderAccessMethod(),
                HTTP_TRANSPORT,
                JSON_FACTORY,
                new GenericUrl(getConfigValue(TOKEN_SERVER_URL, iss)),
                new ClientParametersAuthentication(getConfigValue(API_KEY, iss), ""), getConfigValue(API_KEY, iss), getConfigValue(AUTHORIZATION_SERVER_URL, iss))
                    .setScopes(Arrays.asList(scope))
                    .setDataStoreFactory(DATA_STORE_FACTORY).build();

            // authorize
            LocalServerReceiver receiver = new LocalServerReceiver.Builder().setHost(domain).setPort(port).build();
            Credential cred = new AuthorizationCodeInstalledApp(flow, receiver).authorize(getConfigValue(ROLE, iss));
            // set system property with accessToken
            System.setProperty(getKey(TOKEN, iss), cred.getAccessToken());
            return Results.success("Oauth 'login' successfully.");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            throw new AuthenticationException();
        }
    }

    @Override
    public boolean isEnabled() {
        return StreamSupport.stream(config.getPropertyNames().spliterator(), false)
            .anyMatch(t -> t.startsWith(OAUTH));
    }
    
    private String getConfigValue(String key, Optional<String> iss) {
        return config.getValue(getKey(key, iss), String.class);
    }

    static String getKey(String key, Optional<String> iss) {
        if (iss.isPresent()) {
            key = key.replaceFirst("security\\.oauth2", OAUTH + "." + iss.get());
        }
        return key;
    }
}