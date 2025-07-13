package xyz.quartzframework.spigot.listener;

import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.Plugin;
import xyz.quartzframework.aop.NoProxy;
import xyz.quartzframework.beans.support.annotation.Preferred;
import xyz.quartzframework.beans.support.annotation.Provide;
import xyz.quartzframework.beans.support.annotation.condition.ActivateWhenBeanMissing;
import xyz.quartzframework.listener.ListenerFactory;
import xyz.quartzframework.spigot.session.SpigotSession;
import xyz.quartzframework.stereotype.ContextBootstrapper;

@NoProxy
@RequiredArgsConstructor
@ContextBootstrapper
public class SpigotListenerContextBootstrapper {

    @Provide
    @Preferred
    SpigotPluginEventExecutor pluginEventExecutor(SpigotSession session) {
        return new SpigotPluginEventExecutor(session);
    }

    @Provide
    @ActivateWhenBeanMissing(ListenerFactory.class)
    SpigotListenerFactory listenerFactory(Plugin plugin, SpigotPluginEventExecutor executor) {
        return new SpigotListenerFactory(plugin, executor);
    }
}