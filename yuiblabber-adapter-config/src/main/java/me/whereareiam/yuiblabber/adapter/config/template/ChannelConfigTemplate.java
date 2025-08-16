package me.whereareiam.yuiblabber.adapter.config.template;

import me.whereareiam.yui.api.output.config.DefaultConfig;
import me.whereareiam.yuiblabber.api.model.config.BlabberChannel;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ChannelConfigTemplate implements DefaultConfig<BlabberChannel> {
	@Override
	public BlabberChannel getDefault() {
		BlabberChannel channel = new BlabberChannel();

		// Default values
		channel.setId("example");
		channel.setEnabled(false);
		channel.setChannelIds(List.of("123456789012345678"));
		channel.setConnections(List.of("example"));
		channel.setBalancing(BlabberChannel.BalancingStrategy.RANDOM);

		return channel;
	}
}


