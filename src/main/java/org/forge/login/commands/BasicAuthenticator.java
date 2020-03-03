package org.forge.login.commands;

import java.util.Base64;
import java.util.Optional;
import java.util.stream.StreamSupport;

import javax.inject.Inject;
import javax.naming.AuthenticationException;

import org.eclipse.microprofile.config.Config;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;

/**
 * BasicAuthenticator
 */
public class BasicAuthenticator implements Authenticator {

    private static final String BASIC = "security.basic";
    private static final String USERNAME = BASIC + ".username";
    private static final String PASSWORD = BASIC + ".password";
    private static final String CREDENTIAL = BASIC + ".credential";

    @Inject
    Config config;

	@Override
	public Result authenticate(Optional<String> iss) throws AuthenticationException {
        String usernameKey = getKey(USERNAME, iss);
        String passwordKey = getKey(PASSWORD, iss);

        Optional<String> un = config.getOptionalValue(usernameKey, String.class);
        Optional<String> pw = config.getOptionalValue(passwordKey, String.class);
        if (un.isPresent() && pw.isPresent()) {
            String auth = un.get() + ":" + pw.get();
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            
            System.setProperty(CREDENTIAL, new String(encodedAuth));
            return Results.success("Basic 'login' successfully!");
        } else {
            return Results.fail("no username or password found!");
        }
	}

	@Override
	public boolean isEnabled() {
        return StreamSupport.stream(config.getPropertyNames().spliterator(), false)
            .anyMatch(t -> t.startsWith(BASIC));
	}

    static String getKey(String key, Optional<String> iss) {
        if (iss.isPresent()) {
            key = key.replaceFirst("security\\.basic", BASIC + "." + iss.get());
        }
        return key;
    }
    
}