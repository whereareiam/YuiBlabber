package me.whereareiam.yuiblabber.common.listener;

import lombok.RequiredArgsConstructor;
import me.whereareiam.yuiblabber.common.channel.ChannelRoutingService;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class MessageReceivedListener extends ListenerAdapter {
	private final ChannelRoutingService channelRoutingService;

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (event.getAuthor().isBot())
			return;

		channelRoutingService.handleMessage(event);
	}
}