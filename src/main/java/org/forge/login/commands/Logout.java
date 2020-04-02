package org.forge.login.commands;

import java.util.Optional;

import javax.inject.Inject;

import org.jboss.forge.addon.ui.command.AbstractUICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;

public class Logout extends AbstractUICommand {

	@Inject
	@WithAttributes(label = "iss")
	private UIInput<String> iss;

	@Override
	public UICommandMetadata getMetadata(UIContext context) {
		return Metadata.forCommand(Logout.class)
		.category(Categories.create("security"))
		.name("logout");
	}

	@Override
	public void initializeUI(UIBuilder builder) throws Exception {
		builder.add(iss);
	}

	@Override
	public Result execute(UIExecutionContext context) throws Exception {
		String key = OAuthAuthenticator.getKey(OAuthAuthenticator.TOKEN, Optional.ofNullable(iss.getValue()));
		if (key != null) {
			System.out.println(key);
			System.clearProperty(key);
		} else {
			return Results.fail("not logged in.");
		}
		return Results.success("Command 'logout' successfully executed!");
	}

	public static boolean isLoggedIn(String iss){
		return (System.getProperty(OAuthAuthenticator.getKey(OAuthAuthenticator.TOKEN, Optional.ofNullable(iss))) != null);
	}
}