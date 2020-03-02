package org.forge.login.commands;

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

    @Inject
    Config config;

	@Override
	public Result authenticate(Optional<String> iss) throws AuthenticationException {
        String usernameKey = getKey("security.basic.username", iss);
        String passwordKey = getKey("security.basic.password", iss);

        Optional<String> un = config.getOptionalValue(usernameKey, String.class);
        Optional<String> pw = config.getOptionalValue(passwordKey, String.class);
        if (un.isPresent() && pw.isPresent()) {
            System.setProperty("security.basic.user", un.get());
            System.setProperty("security.basic.pwd", pw.get());
        } else {
            return Results.fail("no username and password found!");
        }
		
		return Results.success("Basic 'login' successfully!");
	}

	@Override
	public boolean isEnabled() {
        return StreamSupport.stream(config.getPropertyNames().spliterator(), false)
            .anyMatch(t -> t.startsWith(BASIC));
	}

    static String getKey(String key, Optional<String> iss) {
        if (iss.isPresent()) {
            key = key.replaceFirst("security\\.basic", "security.basic." + iss.get());
        }
        return key;
    }
    
}