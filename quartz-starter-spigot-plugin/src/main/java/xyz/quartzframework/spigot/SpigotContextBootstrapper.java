package xyz.quartzframework.spigot;

import lombok.RequiredArgsConstructor;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;
import xyz.quartzframework.QuartzPlugin;
import xyz.quartzframework.aop.NoProxy;
import xyz.quartzframework.beans.support.annotation.Preferred;
import xyz.quartzframework.beans.support.annotation.Provide;
import xyz.quartzframework.stereotype.ContextBootstrapper;

@NoProxy
@ContextBootstrapper
@RequiredArgsConstructor
public class SpigotContextBootstrapper {

    private final SpigotQuartzContext context;

    @Provide
    @Preferred
    QuartzPlugin informationMetadata() {
        return context.getInformationMetadata();
    }

    @Provide
    @Preferred
    Plugin plugin() {
        return context.getQuartz().getPlugin();
    }

    @Provide
    @Preferred
    Server server(Plugin plugin) {
        return plugin.getServer();
    }

    @Provide
    @Preferred
    BukkitScheduler bukkitScheduler(Server server) {
        return server.getScheduler();
    }

    @Provide
    @Preferred
    PluginManager pluginManager(Server server) {
        return server.getPluginManager();
    }

    @Provide
    @Preferred
    PluginLoader pluginLoader(Plugin plugin) {
        return plugin.getPluginLoader();
    }
}