package xyz.quartzframework.bungee.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.md_5.bungee.api.plugin.PluginManager;
import xyz.quartzframework.aop.NoProxy;
import xyz.quartzframework.beans.definition.QuartzBeanDefinitionRegistry;
import xyz.quartzframework.beans.support.annotation.Provide;
import xyz.quartzframework.beans.support.annotation.condition.ActivateWhenBeanMissing;
import xyz.quartzframework.event.EventPublisher;
import xyz.quartzframework.stereotype.ContextBootstrapper;
import xyz.quartzframework.tasks.TaskFactory;

@NoProxy
@Slf4j
@RequiredArgsConstructor
@ContextBootstrapper
public class BungeeEventPublisherContextBootstrapper {

    private final TaskFactory taskFactory;

    private final QuartzBeanDefinitionRegistry registry;

    @Provide
    @ActivateWhenBeanMissing(EventPublisher.class)
    EventPublisher eventPublisher(PluginManager pluginManager) {
        return new BungeeEventPublisher(registry, pluginManager, taskFactory);
    }
}