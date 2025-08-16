package me.whereareiam.yuiblabber;

import lombok.AllArgsConstructor;
import me.whereareiam.yui.api.output.plugin.YuiPlugin;
import me.whereareiam.yuiblabber.common.service.ConnectionService;
import org.springframework.context.ApplicationContext;

@AllArgsConstructor
public class YuiBlabber implements YuiPlugin {
	private final ApplicationContext ctx;

	@Override
	public void onEnable() {
		ctx.getBean(ConnectionService.class).warmup();
	}

	@Override
	public void onDisable() {
		ctx.getBean(ConnectionService.class).shutdown();
	}
}
