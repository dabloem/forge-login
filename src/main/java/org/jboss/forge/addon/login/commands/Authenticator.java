package org.jboss.forge.addon.login.commands;

import java.util.Optional;

import javax.naming.AuthenticationException;

import org.jboss.forge.addon.ui.result.Result;

/**
 * Authenticator
 */
public interface Authenticator {

    Result authenticate(Optional<String> iss) throws AuthenticationException;

    boolean isEnabled();
}