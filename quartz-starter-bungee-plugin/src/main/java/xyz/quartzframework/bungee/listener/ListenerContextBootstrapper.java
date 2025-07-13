package xyz.quartzframework.bungee.listener;

import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import xyz.quartzframework.aop.NoProxy;
import xyz.quartzframework.beans.support.annotation.Preferred;
import xyz.quartzframework.beans.support.annotation.Provide;
import xyz.quartzframework.beans.support.annotation.condition.ActivateWhenBeanMissing;
import xyz.quartzframework.bungee.session.BungeeSession;
import xyz.quartzframework.listener.ListenerFactory;
import xyz.quartzframework.stereotype.ContextBootstrapper;

@NoProxy
@RequiredArgsConstructor
@ContextBootstrapper
public class ListenerContextBootstrapper {

    @Provide
    @Preferred
    BungeePluginEventExecutor pluginEventExecutor(BungeeSession session) {
        return new BungeePluginEventExecutor(session);
    }

    @Provide
    @ActivateWhenBeanMissing(ListenerFactory.class)
    BungeeListenerFactory listenerFactory(Plugin plugin, PluginManager pluginManager, BungeePluginEventExecutor executor) {
        return new BungeeListenerFactory(plugin, pluginManager, executor);
    }
}