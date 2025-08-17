package me.whereareiam.yuiblabber.common.channel.factory;

import me.whereareiam.yuisynapse.api.model.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
public class DiscordMessageFactory {
	public Message fromEvent(MessageReceivedEvent event, String connectionId) {
		String channelId = event.getChannel().getId();

		Map<String, Object> metadata = new HashMap<>();
		metadata.put("userId", event.getAuthor().getId());
		metadata.put("channelId", channelId);
		metadata.put("connectionId", connectionId);

		// Mark if this message is a direct reply to a bot message to boost interest gating
		boolean replyToBot = false;
		try {
			net.dv8tion.jda.api.entities.Message referenced = event.getMessage().getReferencedMessage();
			replyToBot = referenced != null && referenced.getAuthor().isBot();
		} catch (Exception ignored) {
		}
		metadata.put("replyToBot", replyToBot);

		Message.UserInfo author = Message.UserInfo.builder()
				.id(event.getAuthor().getId())
				.username(event.getAuthor().getName())
				.displayName(event.getMember() != null ?
						event.getMember().getEffectiveName() :
						event.getAuthor().getName())
				.build();

		return Message.builder()
				.id(event.getMessageId())
				.role(Message.Role.USER)
				.content(event.getMessage().getContentDisplay())
				.timestamp(Instant.now())
				.author(author)
				.metadata(metadata)
				.build();
	}
}


