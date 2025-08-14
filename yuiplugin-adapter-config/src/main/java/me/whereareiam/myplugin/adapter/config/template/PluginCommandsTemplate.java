package me.whereareiam.myplugin.adapter.config.template;

import me.whereareiam.yui.api.model.command.Command;
import me.whereareiam.yui.api.model.command.CommandCooldown;
import me.whereareiam.yui.api.model.config.Commands;
import me.whereareiam.yui.api.output.config.DefaultConfig;
import me.whereareiam.yui.api.type.CommandCategory;

import java.util.List;
import java.util.Map;

public class PluginCommandsTemplate implements DefaultConfig<Commands> {
	@Override
	public Commands getDefault() {
		Commands commands = new Commands();

		// Default values
		Command example = new Command(
				true,
				List.of("example"),
				"translate(commands.example.description)",
				"translate(commands.example.example)",
				"{command} {usage}",
				Map.of(),
				CommandCategory.FUN,
				new CommandCooldown(true, 5, "myplugin")
		);

		commands.addCommand("example", example);

		return commands;
	}
}
