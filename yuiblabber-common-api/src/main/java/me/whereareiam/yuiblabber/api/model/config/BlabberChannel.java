package me.whereareiam.yuiblabber.api.model.config;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class BlabberChannel {
	public enum BalancingStrategy {RANDOM, ROUND_ROBIN}

	private String id;
	private boolean enabled;

	private List<String> channelIds;
	private List<String> connections;

	private BalancingStrategy balancing;
}


