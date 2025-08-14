package me.whereareiam.myplugin.common.command;

import me.whereareiam.yui.api.annotation.Command;
import me.whereareiam.yui.api.output.CommandBase;
import me.whereareiam.yui.api.style.StyleKit;
import me.whereareiam.yui.api.util.Translatable;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

@Component
public class ExampleCommand implements CommandBase {
	@Command(name = "example")
	public void onCommand(SlashCommandInteractionEvent event) {
		event.replyEmbeds(StyleKit.embeds().primary()
						.setTitle(Translatable.of("plugin.yuiplugin.simpleMessage", event.getUser().getIdLong()))
						.build()
				).setEphemeral(true)
				.queue();
	}
}
