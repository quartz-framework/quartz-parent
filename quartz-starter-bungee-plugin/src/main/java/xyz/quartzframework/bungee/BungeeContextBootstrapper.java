package xyz.quartzframework.bungee;

import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import xyz.quartzframework.QuartzPlugin;
import xyz.quartzframework.aop.NoProxy;
import xyz.quartzframework.beans.support.annotation.Preferred;
import xyz.quartzframework.beans.support.annotation.Provide;
import xyz.quartzframework.stereotype.ContextBootstrapper;

@NoProxy
@ContextBootstrapper
@RequiredArgsConstructor
public class BungeeContextBootstrapper {

    private final BungeeQuartzContext context;

    @Provide
    @Preferred
    QuartzPlugin pluginMetadata() {
        return context.getInformationMetadata();
    }

    @Provide
    @Preferred
    Plugin plugin() {
        return context.getQuartz().getPlugin();
    }

    @Provide
    @Preferred
    ProxyServer server(Plugin plugin) {
        return plugin.getProxy();
    }

    @Provide
    @Preferred
    TaskScheduler bukkitScheduler(ProxyServer server) {
        return server.getScheduler();
    }

    @Provide
    @Preferred
    PluginManager pluginManager(ProxyServer server) {
        return server.getPluginManager();
    }
}