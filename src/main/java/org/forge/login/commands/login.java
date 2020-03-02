package org.forge.login.commands;

import java.util.Optional;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.forge.addon.ui.command.AbstractUICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;

public class login extends AbstractUICommand {

	@Inject
	private Instance<Authenticator> authenticators;

	@Inject
	@WithAttributes(label = "iss")
	private UIInput<String> iss;

	@Override
	public UICommandMetadata getMetadata(UIContext context) {
		return Metadata.forCommand(login.class)
			.category(Categories.create("security"))
			.name("login");
	}

	@Override
	public void initializeUI(UIBuilder builder) throws Exception {
		builder.add(iss);
	}

	@Override
	public Result execute(UIExecutionContext context) throws Exception {
		System.out.println( "login for " + iss.getValue() +"..." );

		for (Authenticator authenticator : authenticators) {
			if (authenticator.isEnabled()) {
				Result r = authenticator.authenticate(Optional.ofNullable(iss.getValue()));
				if (!(r instanceof Failed)) {
					return r;
				}
			}
		}

		return Results.fail("Command 'login' failed.");
	}

}