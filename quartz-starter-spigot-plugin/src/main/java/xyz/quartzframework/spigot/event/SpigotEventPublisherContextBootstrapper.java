package xyz.quartzframework.spigot.event;

import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.PluginManager;
import xyz.quartzframework.aop.NoProxy;
import xyz.quartzframework.beans.definition.QuartzBeanDefinitionRegistry;
import xyz.quartzframework.beans.support.annotation.Provide;
import xyz.quartzframework.beans.support.annotation.condition.ActivateWhenBeanMissing;
import xyz.quartzframework.event.EventPublisher;
import xyz.quartzframework.stereotype.ContextBootstrapper;
import xyz.quartzframework.tasks.TaskFactory;

@NoProxy
@RequiredArgsConstructor
@ContextBootstrapper
public class SpigotEventPublisherContextBootstrapper {

    private final TaskFactory taskFactory;

    private final QuartzBeanDefinitionRegistry registry;

    @Provide
    @ActivateWhenBeanMissing(EventPublisher.class)
    EventPublisher eventPublisher(PluginManager pluginManager) {
        return new SpigotEventPublisher(registry, pluginManager, taskFactory);
    }
}