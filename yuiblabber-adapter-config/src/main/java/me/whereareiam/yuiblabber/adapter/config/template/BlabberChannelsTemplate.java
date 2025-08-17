package me.whereareiam.yuiblabber.adapter.config.template;

import me.whereareiam.yui.api.output.config.DefaultConfig;
import me.whereareiam.yuiblabber.api.model.config.channel.BlabberChannel;
import me.whereareiam.yuiblabber.api.model.config.channel.BlabberChannels;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BlabberChannelsTemplate implements DefaultConfig<BlabberChannels> {
	@Override
	public BlabberChannels getDefault() {
		BlabberChannels channels = new BlabberChannels();
		BlabberChannel example = new BlabberChannel();
		example.setId("example");
		example.setEnabled(false);
		example.setChannelIds(List.of("123456789012345678"));
		example.setConnections(List.of("example"));
		example.setBalancing(BlabberChannel.BalancingStrategy.RANDOM);

		channels.setChannels(List.of(example));

		return channels;
	}
}


