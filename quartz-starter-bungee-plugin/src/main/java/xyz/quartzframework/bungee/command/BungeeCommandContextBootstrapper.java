package xyz.quartzframework.bungee.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import xyz.quartzframework.Inject;
import xyz.quartzframework.aop.NoProxy;
import xyz.quartzframework.beans.factory.QuartzBeanFactory;
import xyz.quartzframework.beans.support.annotation.Provide;
import xyz.quartzframework.beans.support.annotation.condition.ActivateWhenBeanMissing;
import xyz.quartzframework.cli.CommandExecutor;
import xyz.quartzframework.cli.picocli.CommandLineDefinition;
import xyz.quartzframework.ordered.Priority;
import xyz.quartzframework.stereotype.ContextBootstrapper;

@Slf4j
@NoProxy
@ContextBootstrapper
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class BungeeCommandContextBootstrapper {

    private final QuartzBeanFactory quartzBeanFactory;

    @Provide
    @Priority(0)
    @ActivateWhenBeanMissing(CommandExecutor.class)
    CommandExecutor commandExecutor(CommandLineDefinition commandLineDefinition) {
        return new BungeeCommandExecutor(quartzBeanFactory, commandLineDefinition);
    }
}
