package me.whereareiam.yuiblabber.common.connection;

import lombok.RequiredArgsConstructor;
import me.whereareiam.yuiblabber.api.model.config.channel.BlabberChannel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
public class ConnectionSelector {
	private final Map<String, Integer> roundRobinIndex = new ConcurrentHashMap<>();

	public String selectConnectionId(BlabberChannel cfg) {
		List<String> ids = cfg.getConnections();
		if (ids == null || ids.isEmpty())
			return null;

		BlabberChannel.BalancingStrategy strategy = Optional.ofNullable(cfg.getBalancing())
				.orElse(BlabberChannel.BalancingStrategy.RANDOM);

		if (strategy == BlabberChannel.BalancingStrategy.ROUND_ROBIN) {
			Integer idx = roundRobinIndex.get(cfg.getId());
			if (idx == null)
				idx = 0;

			int current = idx;
			int next = (current + 1) % ids.size();
			roundRobinIndex.put(cfg.getId(), next);

			return ids.get(current);
		} else {
			return ids.get(ThreadLocalRandom.current().nextInt(ids.size()));
		}
	}
}


